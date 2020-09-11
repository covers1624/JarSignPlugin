package net.covers1624.jarsign;

import groovy.lang.Closure;
import net.covers1624.jarsign.jar.JarSignSpecExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 19/8/20.
 */
public class Utils {

    //32k buffer.
    private static final ThreadLocal<byte[]> bufferCache = ThreadLocal.withInitial(() -> new byte[32 * 1024]);

    /**
     * Copies the content of an InputStream to an OutputStream.
     *
     * @param is The InputStream.
     * @param os The OutputStream.
     * @throws IOException If something is bork.
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = bufferCache.get();
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    /**
     * Reads an InputStream to a byte array.
     *
     * @param is The InputStream.
     * @return The bytes.
     * @throws IOException If something is bork.
     */
    public static byte[] toBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(is, os);
        return os.toByteArray();
    }

    public static Runnable sneakR(ThrowingRunnable<Throwable> tr) {
        return () -> sneaky(tr);
    }

    public static <T, R> Function<T, R> sneakF(ThrowingFunction<T, R, Throwable> tf) {
        return e -> sneaky(() -> tf.apply(e));
    }

    public static <T> Consumer<T> sneakC(ThrowingConsumer<T, Throwable> tc) {
        return t -> {
            try {
                tc.accept(t);
            } catch (Throwable th) {
                throwUnchecked(th);
            }
        };
    }

    public static <T> Action<T> sneakA(ThrowingConsumer<T, Throwable> tc) {
        return e -> sneaky(() -> tc.accept(e));
    }

    public static void sneaky(ThrowingRunnable<Throwable> tr) {
        try {
            tr.run();
        } catch (Throwable t) {
            throwUnchecked(t);
        }
    }

    public static <T> T sneaky(ThrowingProducer<T, Throwable> tp) {
        try {
            return tp.get();
        } catch (Throwable t) {
            throwUnchecked(t);
            return null;//Un possible
        }
    }

    public static File resolveFile(Project project, Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Closure<?>) {
            return resolveFile(project, ((Closure<?>) obj).call());
        }
        if (obj instanceof Supplier<?>) {
            return resolveFile(project, ((Supplier<?>) obj).get());
        }
        return project.file(obj);
    }

    public static String resolveString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Closure<?>) {
            return resolveString(((Closure<?>) obj).call());
        }
        if (obj instanceof Supplier<?>) {
            return resolveString(((Supplier<?>) obj).get());
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof CharSequence) {
            return obj.toString();
        }
        throw new IllegalStateException("Object is not an instance of String or CharSequence.");
    }

    public static Boolean resolveBoolean(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Closure<?>) {
            return resolveBoolean(((Closure<?>) obj).call());
        }
        if (obj instanceof Supplier<?>) {
            return resolveBoolean(((Supplier<?>) obj).get());
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof CharSequence) {
            return Boolean.parseBoolean(obj.toString());
        }
        throw new IllegalStateException("Object is not an instance of Boolean or represents a Boolean value");
    }

    @SuppressWarnings ("unchecked")
    public static <T> void executeDelegate(T delegate, Closure<T> closure) {
        Closure<JarSignSpecExtension> copy = (Closure<JarSignSpecExtension>) closure.clone();
        copy.setResolveStrategy(Closure.DELEGATE_FIRST);
        copy.setDelegate(delegate);
        if (copy.getMaximumNumberOfParameters() == 0) {
            copy.call();
        } else {
            copy.call(delegate);
        }
    }

    @SuppressWarnings ("unchecked")
    public static <T extends Throwable> void throwUnchecked(Throwable t) throws T {
        throw (T) t;
    }

    public interface ThrowingConsumer<T, E extends Throwable> {

        void accept(T thing) throws E;
    }

    public interface ThrowingFunction<T, R, E extends Throwable> {

        R apply(T thing) throws E;
    }

    public interface ThrowingProducer<T, E extends Throwable> {

        T get() throws E;
    }

    public interface ThrowingRunnable<E extends Throwable> {

        void run() throws E;
    }

}
