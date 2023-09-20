package org.example;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //获取程序开始时间
        long startTime = System.nanoTime();
        if(args.length!=3){
            System.out.println("请输入正确地址格式: [原文文件] [抄袭版论文的文件] [答案文件]");
            System.out.println("请确保地址为绝对地址且不含空格");
        }else{
            // 文章文件路径
//        String articlePath1 = "C:\\Users\\23989\\Desktop\\TEST\\test01_a.txt";
//        String articlePath2 = "C:\\Users\\23989\\Desktop\\TEST\\test01_b.txt";
            String articlePath1 = args[0];
            String articlePath2 = args[1];
            // 结果文件路径
//        String answerPath = "C:\\Users\\23989\\Desktop\\TEST\\answer.txt";
            String answerPath = args[2];

            try {
                // 读取文章内容
                String article1 = readFile(articlePath1);
                String article2 = readFile(articlePath2);

                // 计算相似度
                double similarity = calculateSimilarity(article1, article2);

                // 将结果写入文件
                writeResult(answerPath, similarity);
                System.out.printf("Similarity: %.2f%%\n", similarity);
                System.out.println("结果已写入路径: "+answerPath);
            } catch (IOException e) {
                System.out.println("文件读取或写入失败!(请检查路径后重试)");
//                e.printStackTrace();
            }
        }

        //获取程序结束时间并计算时长
        long endTime = System.nanoTime();
        System.out.println("程序总用时: "+((double)(endTime-startTime)/1000000)+"ms");
    }

    // 读取文件内容并去除标点后返回字符串
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();

        // 使用正则表达式去除标点符号
        String punctuationRegex = "[.,!?':;。，！？“”：；]";

        return content.toString().replaceAll(punctuationRegex,"");
    }

    // 计算余弦相似度(串行)
    private static double calculateSimilarity(String article1, String article2) {
        // 统计词频
        Map<String, Integer> wordFrequency1 = getWordFrequency(article1);
        Map<String, Integer> wordFrequency2 = getWordFrequency(article2);

        // 计算向量的内积
        double dotProduct = calculateDotProduct(wordFrequency1, wordFrequency2);
        System.out.println("向量内积: "+dotProduct);

        // 计算向量的模
        double magnitude1 = calculateMagnitude(wordFrequency1);
        double magnitude2 = calculateMagnitude(wordFrequency2);
        System.out.println("向量模: "+"m1= "+magnitude1+"\tm2= "+magnitude2);

        // 计算余弦相似度
        double similarity = dotProduct / (magnitude1 * magnitude2);
        return similarity * 100; // 转换为百分比形式
    }

    // 统计词频
    private static Map<String, Integer> getWordFrequency(String article) {
        Map<String, Integer> wordFrequency = new HashMap<>();

        // 使用HanLP进行中文分词
        List<Term> termList = HanLP.segment(article);

        // 统计词频
        for (Term term : termList) {
            String word = term.word;
            int frequency = wordFrequency.getOrDefault(word, 0);
            wordFrequency.put(word, frequency + 1);
        }
        // 调试代码
//        System.out.println(termList);
//        System.out.println(wordFrequency);

        return wordFrequency;
    }

    // 计算向量的内积
    private static double calculateDotProduct(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        double dotProduct = 0;

        // 遍历两个向量的键集合
        for (String key : vector1.keySet()) {
            if (vector2.containsKey(key)) {
                dotProduct += vector1.get(key) * vector2.get(key);
            }
        }

        return dotProduct;
    }

    // 计算向量的模
    private static double calculateMagnitude(Map<String, Integer> vector) {
        double magnitude = 0;

        // 遍历向量的值集合
        for (int value : vector.values()) {
            magnitude += Math.pow(value, 2);
        }

        return Math.sqrt(magnitude);
    }

    // 将相似度结果写入文件
    private static void writeResult(String filePath, double similarity) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(String.format("Similarity: %.2f%%", similarity));
        writer.close();
    }
}