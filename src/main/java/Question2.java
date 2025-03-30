import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Question2 {



    public static void main(String[] args) {
        var s = "leetcode";
        var wordDict = new String[] {"leet", "code"};

        var dictTrie = new Trie();
        for (var w : wordDict) {
            dictTrie.insertWord(w);
        }

        System.out.print(isMatchInDict(s, dictTrie));
    }

    public static boolean isMatchInDict(String s, Trie trie) {
        var characters = s.toCharArray();
        var queue = new ArrayDeque<Integer>();
        queue.offer(0);

        while (!queue.isEmpty()) {
            var i = queue.poll();
            var pointer = trie.root;
            for (int j = i; j < characters.length; j++) {
                var ch = characters[j];

                if (!pointer.children.containsKey(ch)) return false;

                pointer = pointer.children.get(ch);
                if (pointer.isWord) {
                    queue.offer(j + 1);
                    if (j == characters.length - 1) {
                        return true;
                    }
                    break;
                }

            }
        }

        return false;
    }


    public static class Trie {
        public TrieNode root = new TrieNode();

        public void insertWord(String word) {
            char[] chs = word.toCharArray();
            var point = this.root;
            for (var ch : chs) {
                point.children.putIfAbsent(ch, new TrieNode());
                point = point.children.get(ch);
            }
            point.isWord = true;
        }
    }

    public static class TrieNode {
        public boolean isWord;
        public Map<Character, TrieNode> children = new HashMap<>();
    }

}

