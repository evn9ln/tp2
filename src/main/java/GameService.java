public class GameService {

    public static int[] getWarriors() {
        int[] warriors = new int[8];
        for (int i = 0; i < warriors.length; i++) {
            warriors[i] = (int) (1 + Math.random() * warriors.length);
        }
        return warriors;
    }
}
