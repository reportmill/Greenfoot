package greenfoot;
import java.util.*;

/**
 * Used to store data.
 */
public class UserInfo {
    
    // The current score
    int           _score;
    
    // Shared instance
    static UserInfo _shared;

    /**
     * Returns the User rank.
     */
    public int getRank()  { return 1; }

    /**
     * Returns the User score.
     */
    public int getScore()  { return _score; }

    /**
     * Sets the User score.
     */
    public void setScore(int aValue)  { _score = aValue; }

    /**
     * Return User name.
     */
    public String getUserName()  { return "John Doe"; }

    /**
     * Return User image.
     */
    public GreenfootImage getUserImage()
    {
        return new GreenfootImage("John Doe");
    }

    /**
     * Stores the data.
     */
    public boolean store()  { return true; }

    /**
     * Returns the top n user infos.
     */
    public static List getTop(int aMax)  { return Collections.EMPTY_LIST; }

    /**
     * Returns the top n user infos.
     */
    public static List getNearby(int aMax)  { return Collections.EMPTY_LIST; }

    /**
     * Returns whether storage is available.
     */
    public static boolean isStorageAvailable()  { return false; }

    /**
     * Returns the UserInfo for current user.
     */
    public static UserInfo getMyInfo()
    {
        if (_shared != null) return _shared;
        return _shared=new UserInfo();
    }
}