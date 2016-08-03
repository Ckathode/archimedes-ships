package darkevilmac.archimedes.common.object.achievement;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.ArrayList;


public class SmartAchievementPage {

    private AchievementPage achievementPage;
    private ArrayList<Achievement> registrations;
    private String name;

    public SmartAchievementPage(String name) {
        this.name = name;
        this.registrations = new ArrayList<Achievement>();
    }

    public void registerAchievement(Achievement achievement) {
        achievement.registerStat();
        registrations.add(achievement);
    }

    public void finalize() {
        Achievement[] registrationsArray = new Achievement[registrations.size()];
        registrationsArray = registrations.toArray(registrationsArray);
        achievementPage = new AchievementPage(name, registrationsArray);
        AchievementPage.registerAchievementPage(achievementPage);
    }


}
