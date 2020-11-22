package blockchain.utils;

import java.util.List;
import java.util.Random;

public class RandomMessageGenerator {
    private static Random ran = new Random();

    private static final List<String> MESSAGES = List.of(
            "This is awesome",
            "I love you guys",
            "I don't like it",
            "Did you guys watch The Big Bang Show recently?",
            "I need to get some Star Wars",
            "Is this Nerdtopoia or what??",
            "May the Fourth be with you Young Skywalker",
            "And may the force be with you",
            "I'm getting myself some Chicken"
    );

    public static String getRandomMessage() {
        int messageIndex = Math.abs(ran.nextInt() % MESSAGES.size());
        return String.format("%s", MESSAGES.get(messageIndex));
    }
}
