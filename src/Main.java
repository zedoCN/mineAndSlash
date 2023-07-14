import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static ArrayList<HashSet<Integer>> wordList = new ArrayList<>();
    static HashSet<String> answers = new HashSet<>();//记录所有答案

    static HashSet<Integer> arrays = new HashSet<>(100000);//记录一批排列
    static long progress = 0;
    static int speed = 0; // 用于记录每秒生成的排列的数量
    static int rate = 0; // 用于记录每秒生成的排列的平均数量
    static long remaining = 0; // 用于记录剩余的排列的数量
    static int puzzleLength = 0;//记录输入的长度

    public static void main(String[] args) {
        System.out.println("--挖矿与砍杀解密破解程序--");
        Path path = Path.of("word_list.txt");
        if (Files.exists(path)) {
            try {
                for (String word : Files.readAllLines(path)) {
                    int len = word.length();
                    if (len > wordList.size()) {
                        for (int i = 0; i < len - wordList.size() + 1; i++) {
                            wordList.add(new HashSet<>());
                        }
                    }

                    HashSet<Integer> words = wordList.get(len - 1);
                    words.add(word.hashCode());
                }

            } catch (IOException e) {
                System.err.println("word_list.txt读取失败！");
                throw new RuntimeException(e);
            }
            System.out.println("word_list.txt读取" + wordList.size() + "个词条。");
            Scanner scanner = new Scanner(System.in);
            while (true) {

                System.out.print("输入谜题：");
                String puzzle = scanner.nextLine().toUpperCase();

                answers.clear(); // 初始化答案为空
                generatePermutations(puzzle.toCharArray()); // 生成所有可能的排列，并放入队列中


                if (answers.size() == 0) {
                    System.out.println("未找到相关词条。");
                } else {
                    System.out.println("--找到相关词条--");
                    for (String answer : answers) {
                        System.out.println(answer);
                    }
                }
            }
        } else {
            System.err.println("word_list.txt文件不存在！");
        }
    }

    public static long calculatePermutationsCount(int n) {
        long permutationsCount = 1;
        for (int i = 1; i <= n; i++) {
            permutationsCount *= i;
        }
        return permutationsCount;
    }

    public static void generatePermutations(char[] puzzleChar) {
        puzzleLength = puzzleChar.length;
        long time = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();


        int[] indices = new int[puzzleLength];
        long size = calculatePermutationsCount(puzzleLength);
        System.out.println("总共" + size + "种排列可能。");
        System.out.println("即将在" + wordList.get(puzzleLength - 1).size() + "个词条里搜索。");

        arrays.add(new String(puzzleChar).hashCode());

        int i = 0;

        progress = 0;
        speed = 0; // 用于记录每秒生成的排列的数量
        rate = 0; // 用于记录每秒生成的排列的平均数量
        remaining = size;

        while (i < puzzleLength) { // 如果还没有找到答案，就继续生成排列


            if (System.currentTimeMillis() - time > 1000) {
                time = System.currentTimeMillis();
                rate = speed; // 每隔一秒，就把speed赋值给rate
                speed = 0; // 每隔一秒，就把speed归零
                int remainingTime = (int) (remaining / rate); // 用remaining除以rate，得到一个估计的剩余时间
                System.out.printf("时间：%.1fs 剩余：%d/%d 进度：%.2f%% 速度：%d/s 剩余时间：%ds \n", (time - startTime) / 1000f, progress, size, (float) progress / size * 100f, rate, remainingTime);
            }

            if (indices[i] < i) {
                swap(puzzleChar, i % 2 == 0 ? 0 : indices[i], i);

                arrays.add(new String(puzzleChar).hashCode());


                indices[i]++;
                i = 0;
            } else {
                indices[i] = 0;
                i++;
            }

            if (arrays.size() > 100000) {
                progress += arrays.size();
                speed += arrays.size(); // 每生成一个排列，就把speed加一
                remaining -= arrays.size(); // 每生成一个排列，就把remaining减一
                for (Integer hash : wordList.get(puzzleLength - 1)) {
                    if (arrays.contains(hash)) {
                        answers.add(new String(puzzleChar));
                    }
                }
                arrays.clear();
            }
        }

        for (Integer hash : wordList.get(puzzleLength - 1)) {
            if (arrays.contains(hash)) {
                answers.add(new String(puzzleChar));
            }
        }

    }


    private static void swap(char[] puzzleChar, int i, int j) {
        char temp = puzzleChar[i];
        puzzleChar[i] = puzzleChar[j];
        puzzleChar[j] = temp;
    }
}