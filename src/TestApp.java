import MyHashMap.MyHashMap;

import java.util.Random;

//测试类
public class TestApp {
    public static void main(String[] args) {
        int maxKeyValue = 10000; //假设最大的Key值为10000，存入容量为1000的哈希表中
        MyHashMap map = new MyHashMap(maxKeyValue, 1000);
        fillTestData(map, maxKeyValue, true); //生成maxKeyValue个字符串作为测试的数据对象


        //如果在此处添加断点，使用IDE调试功能查看生成的map.hashTable，可以发现链表都是按值从小到大排列的

        //我们测试查找key值为1234的数据对象
        int searchKey = 1234;
        String result = (String) map.get(searchKey);
        System.out.format("Got data '%s' at key = %d\n", result, searchKey);
        //发现可以找到

        //我们计算哈希表中每一个链表的长度
        int[] dupInfo = map.getDuplicateLength();
        for (int i = 0; i < dupInfo.length; ++i) {
            System.out.format("HashTable[%d] length = %d\n", i, dupInfo[i]);
        }
        // 输出后发现假如使用1~10000不重复的key和data填充哈希表
        // 每个链表的长度几乎都是10=10000/1000，极少数为9或11，说明此哈希表的构造非常理想
        // 假如使用存在重复的1~10000的随机数key和data填充
        // 则会发现哈希表的每一项都有值，并且每一项的链表长度很均匀，普遍在1~5之间，也说明哈希表的构造很理想

    }

    private static void fillTestData(MyHashMap map, int num, boolean randomFlag) {
        for (int i = 0; i < num; ++i) {

            int key;
            Object data;
            if (randomFlag) {
                Random rd = new Random();
                key = rd.nextInt(num);
            } else {
                key = i;
            }
            data = String.format("Test-Data-%d", key);
            if (map.add(key, data)) {
                System.out.format("Added data '%s'\n", data);
            } else {
                System.out.format("Add data '%s' failed\n", data);
            }
        }
    }
}
