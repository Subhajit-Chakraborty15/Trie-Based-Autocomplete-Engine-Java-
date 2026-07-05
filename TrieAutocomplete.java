import java.util.*;

public class TrieAutocomplete {

    // Each node holds up to 26 children (lowercase a-z) and an end-of-word flag.
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEndOfWord = false;
    }

    private final TrieNode root;

    public TrieAutocomplete() {
        root = new TrieNode();
    }

    /** Insert a word into the trie. Ignores null/empty input. */
    public void insert(String word) {
        if (word == null || word.isEmpty()) return;
        word = word.toLowerCase();
        TrieNode current = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';
            if (index < 0 || index >= 26) continue; // skip non a-z chars safely
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    /** Returns true if the exact word exists in the trie. */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /** Returns true if any word in the trie starts with this prefix. */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /**
     * Returns up to `limit` suggestions that start with the given prefix,
     * sorted alphabetically (natural DFS order).
     */
    public List<String> getSuggestions(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        TrieNode prefixNode = findNode(prefix);
        if (prefixNode == null) return results;

        dfsCollect(prefixNode, new StringBuilder(prefix.toLowerCase()), results, limit);
        return results;
    }

    /** Walks down the trie following the given string; returns the ending node or null. */
    private TrieNode findNode(String str) {
        if (str == null) return null;
        str = str.toLowerCase();
        TrieNode current = root;

        for (char ch : str.toCharArray()) {
            int index = ch - 'a';
            if (index < 0 || index >= 26 || current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }

    /** Depth-first collection of all complete words reachable from a node. */
    private void dfsCollect(TrieNode node, StringBuilder path, List<String> results, int limit) {
        if (results.size() >= limit) return;
        if (node.isEndOfWord) results.add(path.toString());

        for (char ch = 'a'; ch <= 'z'; ch++) {
            int index = ch - 'a';
            if (node.children[index] != null) {
                path.append(ch);
                dfsCollect(node.children[index], path, results, limit);
                path.deleteCharAt(path.length() - 1);
                if (results.size() >= limit) return;
            }
        }
    }

    // ---- Demo ----
    public static void main(String[] args) {
        TrieAutocomplete trie = new TrieAutocomplete();
        String[] dictionary = {
            "car", "care", "career", "cat", "catalog", "call", "calm",
            "dog", "dodge", "door", "code", "coder", "coding"
        };

        for (String word : dictionary) trie.insert(word);

        System.out.println("Suggestions for 'ca': " + trie.getSuggestions("ca", 10));
        System.out.println("Suggestions for 'co': " + trie.getSuggestions("co", 10));
        System.out.println("Search 'car' exists? " + trie.search("car"));
        System.out.println("Search 'ar' exists? " + trie.search("ar"));
        System.out.println("Any word starts with 'do'? " + trie.startsWith("do"));
    }
}
