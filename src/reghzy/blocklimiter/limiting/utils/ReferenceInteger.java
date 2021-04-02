package reghzy.blocklimiter.limiting.utils;

public class ReferenceInteger {
    public int value;

    public ReferenceInteger(int value) {
        this.value = value;
    }

    public void increment() {
        this.value++;
    }

    public void decrement() {
        this.value--;
    }
}
