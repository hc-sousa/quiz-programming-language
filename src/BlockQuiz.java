
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class BlockQuiz {
    public String id;
    public List<Block> blocks;
    public boolean goback;
    public Double score;
    public int idx;
    public Double userscore;

    public BlockQuiz(String id, List<Block> blocks, boolean goback) {
        this.id = id;
        this.blocks = blocks;
        this.goback = goback;
        score = 0.0;
        for (Block b : blocks)
            score += b.getScore();
        idx = 0;
        userscore = 0.0;
    }

    public BlockQuiz(String id, boolean goback) {
        this(id, new ArrayList<>(), goback);
    }

    public BlockQuiz(String id) {
        this(id, new ArrayList<>(), true);
    }

    public void add(Block block) {
        String blockId = block.getId();
        for (Block b : blocks) {
            if (b.getId().equals(blockId)) {
                System.out.printf("Block with ID '%s' already exists in quiz '%s'.\n", blockId, id);
                return;
            }
        }
        blocks.add(block);
        score += block.getScore();
    }

    public void remove(Block b) {
        remove(b.getId());
    }

    public void remove(String bId) {
        int i;
        boolean found = false;
        for (i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getId().equals(bId)) {
                found = true;
                break;
            }
        }
        if (found) {
            score -= blocks.get(i).getScore();
            blocks.remove(i);
        } else {
            System.err.printf(
                "Couldn't remove block '%s' from blockquiz '%s': blockquiz has no block with ID '%s'\n",
                bId, id, bId
            );
        }
    }

    public Double getUserScore() {
        Double score = 0.0;
        for (Block block : blocks)
            for (Double qScore : block.getQuestionUserScores())
                score += qScore;
        return score;
    }

    public Double getScore() {
        return score;
    }

    public void execute() {
        Scanner sc = new Scanner(System.in);
        String usage = "Quiz " + id + "\n'>{block ID}' to navigate to another block.\n" + "'.' to submit quiz.";
        System.out.println(usage);
        boolean printBlocks = true;
        String prefix;
        while (true) {
            if (printBlocks)
                System.out.println(blocksString());
            printBlocks = true;
            Block current = blocks.get(idx);
            current.execute();
            System.out.println(blocksString());

            String input = sc.nextLine();
            char startChar = input.charAt(0);
            if (startChar == '.') {
                userscore = getUserScore();
                return;
            } else if (startChar == '>') {
                String blockId = input.substring(1);
                int newIdx = getBlockIdxById(blockId);
                if (!goback && newIdx < idx) {
                    System.out.println("Cannot go back to previous blocks.");
                    printBlocks = false;
                } else {
                    idx = newIdx;
                }
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(blocks);
    }

    private String blocksString() {
        StringBuilder sb = new StringBuilder(id).append(" - blocks:\n");
        String prefix;
        for (int i = 0; i < blocks.size(); i++) {
            if (!goback && i < idx)
                prefix = "# "; // # means the user can't navigate to that block
            else if (i == idx)
                prefix = "> ";
            else
                prefix = "  ";
            sb.append(prefix).append(blocks.get(i).getId()).append("\n");
        }
        return sb.toString();
    }

    private int getBlockIdxById(String bId) {
        for (int i = 0; i < blocks.size(); i++)
            if (blocks.get(i).getId().equals(bId))
                return i;
        System.out.println("Invalid block ID.");
        return idx;
    }
}
