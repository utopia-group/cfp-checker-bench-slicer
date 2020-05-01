import com.google.inject.internal.SingletonScope;
import com.google.inject.Key;
import com.google.inject.util.Providers;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class Harness
{
    private static int nd$int()
    {
        return 1;
    }

    private static void assume(boolean b)
    {
    }

    public static void main(String[] args)
    {
        Harness dummyObj = new Harness();
        SingletonScope s = new SingletonScope();
        Key<Harness> k = Key.get(Harness.class);
        Provider<Harness> p = Providers.of(dummyObj);

        Provider<Harness> p1 = s.scope(k, p);
        int n = nd$int();
        assume(n > 0);
        for (int i = 0; i < n; ++i)
            p1.get();
    }
}
