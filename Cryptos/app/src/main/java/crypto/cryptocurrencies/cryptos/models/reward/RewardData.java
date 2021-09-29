package crypto.cryptocurrencies.cryptos.models.reward;

public class RewardData {

    private int coins;

    private boolean isEarned;

    private int videoCount;

    public RewardData(int coins, boolean isEarned, int videoCount) {
        this.coins = coins;
        this.isEarned = isEarned;
        this.videoCount = videoCount;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean isEarned() {
        return isEarned;
    }

    public void setEarned(boolean earned) {
        isEarned = earned;
    }
}
