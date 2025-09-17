import java.util.*;
import java.io.*;

public class Main {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        // Reader, Writer 사용해주기
        BufferedReader br = new BufferedReader(new FileReader("./w1/test.paren"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("./w1/test.c"));
        //처음 c코드 작성해주기
        bw.write("#include <stdio.h>\nvoid main(){\nunsigned a,b,c;\nunsigned st0,st1,st2,st3,st4;\n");
        //1. paren file 받기
        //2. paren file 분석 ->(<> : 입력값 스택 푸시, () (()) ((())) : 변수, (n -> n push, )<> : 출력, )() : 변수에 저장
        //3. 각 토큰들 나누고 파싱하기?
        //3.1. 스택 활용해서 각 토큰들 넣어주기
        //3.2. 각 토큰 별 파싱해주기

        //() -> a (()) -> b ((())) -> c : 맵 고려

        String paren = br.readLine();
//        LinkedList<String> li = tokenizing(paren);
//        for (String i : li) {
//            System.out.println(i);
//        }
        parsing(tokenizing(paren), bw);
        bw.write("}");
        bw.flush();
        bw.close();
    }
    // 토큰으로 나눠주기 { (, ), (), (()), ((())), +, *, <> }
    static LinkedList<String> tokenizing(String par) {
        LinkedList<String> token_ls = new LinkedList<>();
        int idx = 0;
        while (idx < par.length()) {
            char token = par.charAt(idx);
            // 케이스 (, ), +, *로 나눠서
            if (token == '(') {
                token_ls.add("(");
                idx++;
                int cnt = 0;
                while (idx < par.length() && par.charAt(idx) == '(') {
                    cnt++;
                    idx++;
                }

                if (cnt == 1) {
                    token_ls.add("()");
                    idx++;
                    continue;
                } else if (cnt == 2) {
                    token_ls.add("(())");
                    idx += cnt;
                    continue;
                } else if (cnt == 3) {
                    token_ls.add("((()))");
                    idx += cnt;
                    continue;
                }
                StringBuilder str_num = new StringBuilder();
                while (idx < par.length() && par.charAt(idx) >= '0' && par.charAt(idx) <= '9') {
                    str_num.append(par.charAt(idx));
                    idx++;
                }
                if (!str_num.isEmpty()) {
                    token_ls.add(String.valueOf(str_num));
                    continue;
                }
                token_ls.add("<>");
                idx += 2;
            }
            else if (token == ')'){
                token_ls.add(")");
                idx++;
                int cnt = 0;
                while (idx < par.length() && par.charAt(idx) == '(') {
                    cnt++;
                    idx++;
                }
                if (cnt == 1) {
                    idx++;
                    token_ls.add("()");
                    continue;
                } else if (cnt == 2) {
                    token_ls.add("(())");
                    idx += cnt;
                    continue;
                } else if (cnt == 3) {
                    token_ls.add("((()))");
                    idx += cnt;
                    continue;
                }
                token_ls.add("<>");
                idx += 2;
            }
            else if (token == '+') {
                token_ls.add("+");
                idx++;
            }
            else {
                token_ls.add("*");
                idx++;
            }
        }
        return token_ls;
    }
    static void parsing(LinkedList<String> token_ls, BufferedWriter bw) throws IOException {
        Stack<String> stack = new Stack<>();
        boolean pushing = false;
        boolean poping = false;
        int a = 0;
        int b = 0;
        int c = 0;
        for (String token : token_ls) {
            if (token.equals("(")) {
                pushing = true;
            }
            else if (token.equals(")")) {
                poping = true;
            }
            else if (token.equals("()")) {
                String s = "";
                if (pushing) {
                    s = String.format("st%d = %s;", stack.size(), "a");
                    stack.add(Integer.toString(a));
                    pushing = false;
                    bw.write(s + "\n");
                }
                else if (poping){
                    a = Integer.parseInt(stack.pop());
                    s = String.format("a = st%d;", stack.size());
                    poping = false;
                    bw.write(s + "\n");
                }

            }
            else if (token.equals("(())")) {
                String s;
                if (pushing) {
                    s = String.format("st%d = %s;", stack.size(), "b");
                    stack.add(Integer.toString(b));
                    pushing = false;
                    bw.write(s + "\n");
                }
                else if (poping) {
                    b = Integer.parseInt(stack.pop());
                    s = String.format("b = st%d;", stack.size());
                    poping = false;
                    bw.write(s + "\n");
                }
            }
            else if (token.equals("((()))")) {
                String s;
                if (pushing) {
                    s = String.format("st%d = %s;", stack.size(), "c");
                    stack.add(Integer.toString(c));
                    pushing = false;
                    bw.write(s + "\n");
                }
                else if (poping) {
                    c = Integer.parseInt(stack.pop());
                    s = String.format("c = st%d;", stack.size());
                    poping = false;
                    bw.write(s + "\n");
                }
            }
            else if (token.equals("<>")) {
                if (pushing) {
                    StringBuilder sb = new StringBuilder("scanf(\"%u\", &st");
                    sb.append(stack.size());
                    sb.append(");");
                    stack.add(sc.nextLine());
                    bw.write(String.valueOf(sb) + "\n");
                    pushing = false;
                }
                else if (poping){
                    System.out.println(stack.pop());
                    StringBuilder sb = new StringBuilder("printf(\"%u\\n\", st");
                    sb.append(stack.size());
                    sb.append(");");
                    bw.write(String.valueOf(sb) + "\n");
                    poping = false;
                }
            }
            else if (token.equals("+")) {
                int size1 = stack.size() - 1;
                int size2 = stack.size() - 2;
                int x = Integer.parseInt(stack.pop());
                int y = Integer.parseInt(stack.pop());
                stack.add(Integer.toString(x + y));
                String s = String.format("st%d = st%d + st%d;", size2, size1, size2);
                bw.write(s + "\n");
            }
            else if (token.equals("*")) {
                int size1 = stack.size() - 1;
                int size2 = stack.size() - 2;
                int x = Integer.parseInt(stack.pop());
                int y = Integer.parseInt(stack.pop());
                stack.add(Integer.toString(x * y));
                String s = String.format("st%d = st%d * st%d;", size2, size1, size2);
                bw.write(s + "\n");
            }
            else {
                stack.add(token);
                String s = String.format("st%d = %s;", stack.size() - 1, stack.peek());
                pushing = false;
                bw.write(s + "\n");
            }
        }
    }
}