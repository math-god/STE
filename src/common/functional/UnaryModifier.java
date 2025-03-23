package common.functional;

@FunctionalInterface
public interface UnaryModifier<T> {
    void modify(T arg);
}
