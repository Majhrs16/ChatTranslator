package majhrs16.cht.util;

@FunctionalInterface
public interface RunnableWithTriException<A extends Throwable, B extends Throwable, C extends Throwable> {
	void run() throws A, B, C;
}