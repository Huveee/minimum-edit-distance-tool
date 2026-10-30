# Minimum Edit Distance Tool

A Java Swing desktop app that uses the Minimum Edit Distance (MED) algorithm to suggest similar Turkish words and to show how one word turns into another. Written as a course project in Natural Language Processing.

MED is the smallest number of insertions, deletions and substitutions needed to turn one word into another. The app calculates it with dynamic programming, using a cost matrix.

## Features

- **Part 1, closest words:** enter a word, and the app compares it with every word in `vocabulary_tr.txt` (about 32,000 Turkish words) and lists the five closest.
- **Part 2, transformation steps:** enter two words, and the app shows the edit distance and the steps between them.

Both parts also show the execution time in nanoseconds.

## Running it

You need JDK 8 or newer. Run these from the repository root, because the app looks for `vocabulary_tr.txt` in the current folder:

```bash
javac -encoding UTF-8 -d out src/minEditDistance.java
java -Dfile.encoding=UTF-8 -cp out minEditDistance
```

## Example results

**Part 1:** `welcome`

```text
Closest words:
gelme: 3
tercüme: 3
kelime: 3
belleme: 3
belirme: 3
```

**Part 2:** `merhaba` to `selam`, with steps listed in backtracking order

```text
Transformation Steps:
Substitution a with m
Delete b
Substitution h with l
Delete r
Substitution m with s

Minimum Edit Distance: 5
```

The full algorithm description and test results are in the [project report](docs/med-algorithm-report.pdf).
