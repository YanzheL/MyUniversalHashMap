package MyHashMap;

import java.util.Random;
import java.util.function.Function;

//Function<T,R>类是一个通用的泛型接口，用于存放一个参数类型T，返回类型R的函数
//此处使用主要是方便构造的匿名哈希函数以闭包的形式返回(因为Java中函数不能单独存在，必须依附于类/接口)

public class MyHashMap {
    private ListNode[] hashTable; //哈希表，为链表数组
    private Function<Integer, Integer> hashFunc; //选用的哈希函数

    public MyHashMap(int maxKeyValue, int tableSize) {
        this.hashTable = new ListNode[tableSize];
        //随机从全域散列函数簇里选择一个，以后都使用该函数作为散列函数
        this.hashFunc = new UniversalHash(maxKeyValue, tableSize).randomChooseFunc();
    }

    //将主键key的对象item加入到哈希表中
    public boolean add(int key, Object item) {
        ListNode itemEntry = new ListNode(key, item);
        int hashID = hashFunc.apply(itemEntry.key);
        hashTable[hashID] = addToLink(hashTable[hashID], itemEntry);
        return hashTable[hashID] != null;
    }

    //将元素按照key值的升序加入到链表中，返回链表的头指针
    private ListNode addToLink(ListNode head, ListNode dataNode) {
        if (head == null) {
            return dataNode;
        } else {
            ListNode p = head;
            //指针指向链表尾部
            while (p.next != null) {
                p = p.next;
            }
            //如果尾部节点的key比待加入节点的key小，则直接添加在尾部，否则将原来的尾部节点和待加入节点互换。保证整个链表按照key值升序排列
            if (p.key < dataNode.key) {
                p.next = dataNode;
                dataNode.prev = p;
            } else if (p.key > dataNode.key) {
                dataNode.next = p;
                dataNode.prev = p.prev;
                p.prev = dataNode;
                p.next = null;
            }
            return head;
        }
    }

    //从哈希表中根据key值返回对应的对象，如果没找到则返回null
    public Object get(int key) {
        int hashID = hashFunc.apply(key);
        ListNode p = hashTable[hashID];
        while (p != null && p.key != key) { //如果哈希表对应位置没有节点或者链表里找不到key值，都返回null
            p = p.next;
        }
        return p == null ? p : p.data;
    }

    //获取哈希表中每一项的链表长度，最理想的情况是每一个链表几乎等长，都等于maxKeyValue/tableSize
    public int[] getDuplicateLength() {
        int[] dupInfo = new int[hashTable.length];
        for (int i = 0; i < hashTable.length; ++i) {
            dupInfo[i] = getLinkLength(hashTable[i]);
        }
        return dupInfo;
    }

    //获取单个链表的长度
    private int getLinkLength(ListNode p) {
        int count = 0;
        while (p != null) {
            ++count;
            p = p.next;
        }
        return count;
    }

    //内部类，定义哈希表每一项的数据结构，采用双向链表
    class ListNode {
        int key; //主键
        Object data; //数据段
        ListNode prev, next; //指向前驱和后继的对象引用

        ListNode(int key, Object data) { //构造函数，引用对象自动初始化为null
            this.key = key;
            this.data = data;
        }
    }
}


//全域散列法，生成一簇散列函数，然后MyHashMap使用的时候随机选择其中一个散列函数。此后所有的操作都使用该函数来作用
class UniversalHash {
    //大素数作为种子，此大素数的要求是0~primeSeed-1的范围内应该包含所有的key，也就是说primeSeed应该取大于最大的key的最小素数
    private int primeSeed;
    //哈希表大小
    private int tableSize;

    UniversalHash(int maxKeyValue, int tableSize) {
        this.primeSeed = genPrimeSeed(maxKeyValue);
        this.tableSize = tableSize;
    }

    //返回大于maxKeyValue的一个最小素数
    private static int genPrimeSeed(int maxKeyValue) {
        for (int i = maxKeyValue + 1; ; ++i) {
            if (isPrime(i))
                return i;
        }
    }

    //判断一个数是否是素数
    private static boolean isPrime(int n) {
        int div = (int) Math.sqrt(n) + 1;
        for (int i = 2; i <= div; ++i) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    //返回参数a、b所确定的哈希函数，使用匿名函数实例化Function<Integer, Integer>接口
    public Function<Integer, Integer> getFunc(int a, int b) {
        // 参数a,b确定了一簇哈希函数，使用时从这些函数中随机选一个固定下来作为哈希函数，a、b有范围约束，在下文已给出范围
        // H(a,b) = ((ak+b)mod p)mod m, a的范围是[1,p-1], b的范围是[0,p-1], 其中p为primeSeed素数种子
        // 利用数论知识可以证明，对于任意h(k) 属于 H(a,b)函数簇，概率P(h(k)==h(l)) < 1/m 其中m为哈希表的大小tableSize
        // 对于每个关键字k，设随机变量Yk表示 使h(k) == h(l)的其他非k关键字l的数目
        //                     l
        // 可证明期望 E(Yk) <= SUM(1/m)
        //                   l!=k
        return (k) -> ((a * k + b) % primeSeed) % tableSize;
    }

    //在a、b的范围内随机生成一对a、b参数，然后返回由a、b参数所构造的哈希函数闭包
    public Function<Integer, Integer> randomChooseFunc() {
        Random rd = new Random();
        int a = 1 + rd.nextInt(primeSeed - 1); //a的范围是[1,primeSeed-1]
        int b = rd.nextInt(primeSeed); //b的范围是[0,primeSeed-1]
        return getFunc(a, b);
    }
}