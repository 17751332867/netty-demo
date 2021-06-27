package cn.itcast.learn;

class Something {
 
    // constructor methods
    Something() {}
 
    Something(String something) {
	System.out.println(something);
    }
 
    // static methods
    static String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
    
    // object methods
    String endWith(String s) {
        return String.valueOf(s.charAt(s.length()-1));
    }
    
    void endWith() {}


    public static void main(String[] args) {
        // static methods
        IConvert<String, String> convert = Something::startsWith;
        System.out.println(convert.convert("123"));

        // object methods
        Something something = new Something();
        IConvert<String, String> convert2 = something::endWith;
        System.out.println(convert2.convert("Java"));


        // constructor methods
//        IConvert<String, Something> convert3 = Something::new;
//        System.out.println(convert3.convert("constructors"));
        IConvert<String ,Something>  convert4= Something::new;
        System.out.println(convert4.convert("hh"));
    }
}