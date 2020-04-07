package edu.utexas.cs.utopia.cfpchekcer;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.config.FileOfClasses;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.util.Chain;
import soot.util.JasminOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.ibm.wala.ipa.slicer.Slicer.*;

public class BenchSlicer
{
    private static boolean jimpleOutput = false;

    private static final String EXCLUSIONS =
            "java\\/awt\\/.*\n"
                    + "javax\\/swing\\/.*\n"
                    + "sun\\/awt\\/.*\n"
                    + "sun\\/swing\\/.*\n"
                    + "com\\/sun\\/.*\n"
                    + "sun\\/.*\n"
                    + "org\\/netbeans\\/.*\n"
                    + "org\\/openide\\/.*\n"
                    + "com\\/ibm\\/crypto\\/.*\n"
                    + "com\\/ibm\\/security\\/.*\n"
                    + "org\\/apache\\/xerces\\/.*\n"
                    + "java\\/security\\/.*\n"
                    + "android\\/os\\/.*\n"
                    + "android\\/util\\/.*\n"
                    + "android\\/graphics\\/.*\n"
                    + "android\\/animation\\/.*\n"
                    + "";

    private static Set<String> reachableClasses = new HashSet<>();

    private static Map<String, Set<String>> reachableMethods = new HashMap<>();

    public static Map<String, Map<String, Set<Integer>>> calculateSlicingInfo(String inputData, String className, String... methodNames) throws CancelException, WalaException, IOException, InvalidClassFileException
    {
        Map<String, Map<String, Set<Integer>>> rv = new HashMap<>();
        AnalysisScope scope = AnalysisScopeReader.readJavaScope(inputData, null, BenchSlicer.class.getClassLoader());
        scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes("UTF-8"))));

        IClassHierarchy cha = ClassHierarchyFactory.make(scope);
        Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(
                        scope, cha);

        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);

        CallGraphBuilder<InstanceKey> builder = //Util.makeVanillaNCFABuilder(2,options, new AnalysisCacheImpl(), cha, scope);//Util.makeRTABuilder(options, new AnalysisCacheImpl(), cha, scope);//
                Util.makeZeroOneCFABuilder(Language.JAVA, options, new AnalysisCacheImpl(), cha, scope);
        CallGraph cg = builder.makeCallGraph(options, null);

        for (CGNode n : cg)
        {
            IMethod method = n.getMethod();
            IClass declaringClass = method.getDeclaringClass();
            if (declaringClass.getClassLoader()
                    .getReference()
                    .equals(ClassLoaderReference.Application))
            {
                String sootClassName = declaringClass.getName().toString().substring(1).replace('/', '.');
                reachableClasses.add(sootClassName);

                if (!reachableMethods.containsKey(sootClassName))
                    reachableMethods.put(sootClassName, new HashSet<>());

                reachableMethods.get(sootClassName).add(method.getName().toString());
            }
        }

        //Collection<Statement> stmts = SlicerUtil.findCallTo(cg, methodName, "Landroid/os/PowerManager$WakeLock");
        for (String methodName : methodNames)
        {
            Collection<Statement> stmts = SlicerUtil.findCallTo(cg, methodName, className);
            //Statement s = SlicerUtil.findCallTo(main, "println");
            for (Statement s : stmts)
            {
                if (!s.getNode().getMethod().toString().contains("Application"))
                    continue;

                // compute a data slice
                final PointerAnalysis<InstanceKey> pointerAnalysis = builder.getPointerAnalysis();

                System.out.print("Calculating slice... ");
                //Collection<Statement> slice = Slicer.computeBackwardSlice(s, cg, pointerAnalysis, DataDependenceOptions.FULL, ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);

                CISlicer slicer = new CISlicer(cg, pointerAnalysis, DataDependenceOptions.NO_HEAP, ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
                Collection<Statement> slice = slicer.computeBackwardThinSlice(s);
                System.out.println("done");
//                SlicerUtil.dumpSlice(slice);
                for (Statement stmt : slice)
                {
                    if (stmt instanceof StatementWithInstructionIndex)
                    {
                        CGNode node = stmt.getNode();
                        IMethod method = node.getMethod();
                        IClass declaringClass = method.getDeclaringClass();
                        if (declaringClass.getClassLoader()
                                .getReference()
                                .equals(ClassLoaderReference.Application)) {

                            int bcIndex, instructionIndex = ((StatementWithInstructionIndex) stmt).getInstructionIndex();
                            bcIndex = ((ShrikeBTMethod) method).getBytecodeIndex(instructionIndex);

                            String sootClassName = declaringClass.getName().toString().substring(1).replace('/', '.');

                            if (!rv.containsKey(sootClassName))
                                rv.put(sootClassName, new HashMap<>());

                            Map<String, Set<Integer>> methLocs = rv.get(sootClassName);

                            String methName = method.getName().toString();

                            if (!methLocs.containsKey(methName))
                                methLocs.put(methName, new HashSet<>());

                            methLocs.get(methName).add(method.getLineNumber(bcIndex));
                        }
                    }
                }
            }
        }

        return rv;
    }

    private static int argsSplitIndex(String[] args)
    {
        for (int i = 0; i < args.length; ++i)
            if (args[i].equals("--"))
                return i;

        return -1;
    }

    private static void checkForDanglingUsages(Body mBody)
    {
        Set<Value> mBodyUses = mBody.getUseBoxes().stream().map(ValueBox::getValue).collect(Collectors.toSet());
        Set<Value> mBodyDefs = mBody.getDefBoxes().stream().map(ValueBox::getValue).collect(Collectors.toSet());

        for (Local l : mBody.getLocals())
        {
            if (mBodyUses.contains(l) && !mBodyDefs.contains(l))
                if (!l.getType().toString().contains("Throwable") && !l.getType().toString().contains("Exception"))
                    throw new RuntimeException("Invalid slicing: " + l + " used but not defined");
        }
    }

    private static Set<Unit> getUnitsWithDanglingUsages(Body mBody, Set<Unit> killedUnits)
    {
        Set<Value> mBodyDefs = mBody.getDefBoxes().stream().map(ValueBox::getValue).collect(Collectors.toSet());
        Set<Value> killedDefs = killedUnits.stream().flatMap(u -> u.getDefBoxes().stream()).map(ValueBox::getValue).collect(Collectors.toSet());
        Set<Unit> units = new HashSet<>();
        for (Unit u : mBody.getUnits())
        {
            Set<Value> danglingUses = u.getUseBoxes()
                                       .stream()
                                       .map(ValueBox::getValue)
                                       .filter(killedDefs::contains)
                                       .filter(v -> !mBodyDefs.contains(v))
                                       .collect(Collectors.toSet());

            if (!danglingUses.isEmpty())
                units.add(u);

        }

        return units;
    }

    public static void main(String[] args) throws Exception
    {
        int splitIndex = argsSplitIndex(args);

        String[] sootOptions = new String[]{};

        if (splitIndex != -1)
        {
            sootOptions = Arrays.copyOfRange(args, splitIndex + 1, args.length);
        }

        Map<String, Map<String, Set<Integer>>> slicesLocInfo = calculateSlicingInfo(args[0], args[1], Arrays.copyOfRange(args, 2, splitIndex != -1 ? splitIndex : args.length));

        PackManager packManager = PackManager.v();
        Options sootCmdLine = Options.v();

        sootCmdLine.parse(sootOptions);

        // Do not convert code to BAF
        sootCmdLine.set_output_format(Options.output_format_jimple);

        Scene.v().loadNecessaryClasses();
        packManager.runPacks();

        Set<Value> sliceUses = new HashSet<>();
        for (SootClass c : Scene.v().getApplicationClasses())
        {
            String className = c.getName();
            if (!reachableClasses.contains(className)) continue;

            Map<String, Set<Integer>> methLocs = slicesLocInfo.get(className);
            for (SootMethod m : c.getMethods())
            {
                Set<Integer> locs = methLocs != null ? methLocs.get(m.getName()) : null;

                if (!m.hasActiveBody()) continue;
                Body mBody = m.getActiveBody();

                Chain<Trap> traps = mBody.getTraps();

                UnitPatchingChain units = mBody.getUnits();
                if (traps.size() > 0)
                {
                    Set<Unit> handlerHeads = traps.stream().map(Trap::getHandlerUnit).collect(Collectors.toSet());
                    traps.clear();

                    BlockGraph bGraph = new CompleteBlockGraph(mBody);
                    Set<Unit> unitsToRemove = bGraph.getBlocks()
                                                    .stream()
                                                    .filter(b -> handlerHeads.contains(b.getHead()))
                                                    .flatMap(b ->
                                                             {
                                                                 Set<Unit> bUnits = new HashSet<>();
                                                                 for (Unit u : b)
                                                                     bUnits.add(u);
                                                                 return bUnits.stream();
                                                             })
                                                    .collect(Collectors.toSet());

                    do
                    {
                        unitsToRemove.forEach(units::remove);
                        unitsToRemove = getUnitsWithDanglingUsages(mBody, unitsToRemove);
                    } while (unitsToRemove.size() > 0);

                    checkForDanglingUsages(mBody);
                }

                for (Unit u : units)
                {
                    Set<Value> uses = u.getUseBoxes().stream()
                            .map(ValueBox::getValue).collect(Collectors.toSet());
                    if (locs != null && locs.contains(u.getJavaSourceStartLineNumber()))
                    {
                        sliceUses.addAll(uses);
                    }
                    else if (u instanceof ReturnStmt || u instanceof  ReturnVoidStmt || u instanceof ThrowStmt)
                    {
                        sliceUses.addAll(uses);
                    }
                }
            }
        }

        Set<Unit> slicesDefs = new HashSet<>();
        int oldSz;
        do {
            oldSz = slicesDefs.size();
            for (SootClass c : Scene.v().getApplicationClasses())
            {
                String className = c.getName();
                if (!reachableClasses.contains(className)) continue;

                for (SootMethod m : c.getMethods()) {
                    if (!m.hasActiveBody()) continue;

                    Body mBody = m.getActiveBody();
                    for (Unit u : mBody.getUnits()) {
                        Set<Value> defs = u.getDefBoxes().stream().map(ValueBox::getValue).collect(Collectors.toSet());
                        if (!Collections.disjoint(sliceUses, defs))
                        {
                            slicesDefs.add(u);
                            sliceUses.addAll(u.getUseBoxes().stream().map(ValueBox::getValue).collect(Collectors.toSet()));
                        }
                    }
                }
            }
        } while(slicesDefs.size() != oldSz);

        for (SootClass c : Scene.v().getApplicationClasses())
        {
            String className = c.getName();
            if (!reachableClasses.contains(className)) continue;

            Map<String, Set<Integer>> methLocs = slicesLocInfo.get(className);
            for (SootMethod m : c.getMethods())
            {
                String methName = m.getName();
                if (m.hasActiveBody())
                {
                    Set<Integer> sliceLocs = methLocs != null ? methLocs.get(methName) : null;

                    Set<Unit> unitsToRemove = new HashSet<>();
                    UnitPatchingChain units = m.getActiveBody().getUnits();

                    for (Unit u : units)
                    {
                        int unitJavaLineNum = u.getJavaSourceStartLineNumber();
                        if (u instanceof IdentityStmt ||
                            u instanceof  ReturnVoidStmt ||
                            u instanceof  ReturnStmt ||
                            u instanceof ThrowStmt ||
                            (sliceLocs != null && sliceLocs.contains(unitJavaLineNum)) || slicesDefs.contains(u)) continue;

                        unitsToRemove.add(u);
                    }

                    //Body oldBody = new JimpleBody(m.getActiveBody());
                    unitsToRemove.forEach(units::remove);
                    checkForDanglingUsages(m.getActiveBody());
                }
            }
        }


        for (SootClass c : Scene.v().getApplicationClasses())
        {
            String className = c.getName();
            if (!reachableClasses.contains(className)) continue;

            String fileName = SourceLocator.v().getFileNameFor(c, jimpleOutput ? Options.output_format_jimple : Options.output_format_class);
            Path file = Paths.get(fileName);
            Files.createDirectories(file.getParent());

            Files.deleteIfExists(file);
            if (jimpleOutput)
            {
                try (PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(Files.createFile(file)))))
                {
                    Printer.v().printTo(c, writerOut);
                }
                catch (FileNotFoundException e)
                {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
            else
            {
                try (PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(new JasminOutputStream(Files.newOutputStream(Files.createFile(file))))))
                {
                    JasminClass jasminClass = new soot.jimple.JasminClass(c);
                    jasminClass.print(writerOut);
                    writerOut.flush();
                }
                catch (Throwable e)
                {
                    System.err.println("Failed to translate: " + className);
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                    Files.deleteIfExists(file);
                }
            }
        }
    }
}
