package edu.utexas.cs.utopia.cfpchekcer;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.strings.Atom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static com.ibm.wala.ipa.slicer.Slicer.*;

public class BenchSlicer
{
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
                    //+ "com\\/google\\/android\\/.*\n"
                    //+ "com\\/android\\/.*\n"
                    //+ "android\\/.*\n"
                    //+ "org\\/.*\n"
                    + "";

    public static void doSlicing(String inputData, String mainClass) throws CancelException, WalaException, IOException
    {
        AnalysisScope scope = AnalysisScopeReader.readJavaScope(inputData, null, BenchSlicer.class.getClassLoader());
        scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes("UTF-8"))));

        IClassHierarchy cha = ClassHierarchyFactory.make(scope);
        Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(
                        scope, cha);

        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);// CallGraphTestUtil.makeAnalysisOptions(scope, entrypoints);

        CallGraphBuilder<InstanceKey> builder = Util.makeRTABuilder(options, new AnalysisCacheImpl(), cha, scope);//Util.makeNCFABuilder(4,options, new AnalysisCacheImpl(), cha, scope);
                //Util.makeZeroOneCFABuilder(Language.JAVA, options, new AnalysisCacheImpl(), cha, scope);
        CallGraph cg = builder.makeCallGraph(options, null);

        CGNode main = CallGraphSearchUtil.findMainMethod(cg);

        Collection<Statement> stmts = SlicerUtil.findCallTo(cg, "release");
        //Statement s = SlicerUtil.findCallTo(main, "println");
        for (Statement s : stmts) {
            if (!s.getNode().getMethod().toString().contains("Application"))
                continue;
            System.err.println("Statement: " + s);
            // compute a data slice
            final PointerAnalysis<InstanceKey> pointerAnalysis = builder.getPointerAnalysis();
            com.ibm.wala.ipa.slicer.thin.CISlicer slicer = new CISlicer(cg, pointerAnalysis, DataDependenceOptions.NO_HEAP, ControlDependenceOptions.NONE);
            Collection<Statement> slice = slicer.computeBackwardThinSlice(s);

            SlicerUtil.dumpSlice(slice);

            int i = 0;
            for (Statement st : slice) {
                if (st.getNode()
                        .getMethod()
                        .getDeclaringClass()
                        .getClassLoader()
                        .getReference()
                        .equals(ClassLoaderReference.Application)) {
                    i++;
                }
            }
        }

    }


    public static void main(String[] args) throws Exception
    {
        doSlicing("wala.input.txt", "LActivityHarness");
    }
}
