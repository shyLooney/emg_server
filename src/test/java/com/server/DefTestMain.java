package com.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class DefTestMain {
    @Data
    @AllArgsConstructor
    static class Node {
        String name;
        int age;

//        @Override
//        public String toString() {
//            return "Node{" +
//                    "name='" + name + '\'' +
//                    ", age=" + age +
//                    '}';
//        }
    }
    public static void main(String[] args) {
        Map<Node, Integer> map = new HashMap<>();

        Node node = new Node("sema", 11);
        Node test = new Node("sema", 11);
        map.put(node, 1);

        node.setAge(13);
        node.setName("vasya");
        map.put(node, 2);

        node.setAge(17);
        node.setName("lola");
        map.put(node, 3);

        System.out.println("Keys:");
        for (var item : map.keySet())
            System.out.println(item);

        System.out.println("Values:");
        for (var item : map.values())
            System.out.println(item);

        System.out.println(map.get(test));
    }
}
