package common.infrastructure.functional;

@FunctionalInterface
public interface UnaryModifier<T> {
    void modify(T arg);
}
