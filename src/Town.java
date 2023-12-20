/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean searchedTown;
    private boolean goldDig;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.treasure = getTreasure();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        goldDig = false;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown(boolean easyMode) {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak() && !easyMode) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item + ".";
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You left the shop.";
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = "You want trouble, stranger!  You got it!\n" + Colors.RED + "Oof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                if (goldDiff > hunter.getGold()) {
                    System.out.print("\nGame Over");

                    System.exit(0);
                }
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                hunter.changeGold(-goldDiff);
            }
        }
    }
    public void digForGold() {
        if (hunter.hasItemInKit("shovel")) {
            int Chance =  (int) (Math.random() * 2);
            if (Chance == 0 && !goldDig) {
                int goldDiff = (int) (Math.random() * 20) + 1;
                printMessage = "You dug up " + goldDiff + " gold";
                hunter.changeGold(goldDiff);
                goldDig = true;
            } else if (!goldDig) {
                printMessage = "You dug but only found dirt";
                goldDig = true;
            } else {
                printMessage = "You already dug for gold in this town";
            }
        } else {
            printMessage = "You can't dig for gold without a shovel";
        }
    }

    public String getTreasure() {
        double rnd = Math.random();
        if (rnd < .25) {
            return "crown";
        } else if (rnd < .5) {
            return "trophy";
        } else if (rnd < .75) {
            return "gem";
        } else {
            return "dust";
        }
    }

    public void huntForTreasure() {
        if (searchedTown) {
           printMessage = "You have already searched this town.";
        } else {
            if (treasure.equals("dust")) {
                printMessage = "You found dust";
            } else {
                if (hunter.hasTreasure(treasure)) {
                    printMessage = "You already collected " + treasure;
                } else {
                    printMessage = "You found a " + treasure + "!";
                    hunter.addTreasure(treasure);
                }
                searchedTown = true;
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .16) {
            return new Terrain(Colors.CYAN + "Mountains" + Colors.RESET, "Rope");
        } else if (rnd < .33) {
            return new Terrain(Colors.CYAN + "Ocean" + Colors.RESET, "Boat");
        } else if (rnd < .5) {
            return new Terrain(Colors.CYAN + "Plains" + Colors.RESET, "Horse");
        } else if (rnd < .66) {
            return new Terrain(Colors.CYAN + "Desert" + Colors.RESET, "Water");
        } else if (rnd < .83){
            return new Terrain(Colors.CYAN + "Jungle" + Colors.RESET, "Machete");
        } else {
            return new Terrain(Colors.CYAN + "Marsh" + Colors.RESET, "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}