package common.utility;

import java.util.List;

public class CommonUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static <T> T getElementOrNull(List<T> list, int index) {
        if (list.isEmpty()) return null;
        if (index < 0 || index > list.size() - 1) return null;

        return list.get(index);
    }

    public static int getSum(List<Integer> numbers, int from, int to) {
        var sum = 0;
        var boundedArray = boundList(numbers, from, to);
        for (var num : boundedArray) {
            sum += num;
        }
        return sum;
    }

    private static int[] boundList(List<Integer> numbers, int from, int to) {
        var newArray = new int[to - from];
        var newArrayIndex = 0;
        for (int i = from; i < to; i++) {
            newArray[newArrayIndex] = numbers.get(i);
            newArrayIndex++;
        }

        return newArray;
    }
}
