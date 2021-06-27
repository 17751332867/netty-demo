package cn.itcast.learn;

@FunctionalInterface
    interface IConvert<F, T> {
        T convert(F form);
    }