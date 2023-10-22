

   import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private double carryWeightCapacity;
    private Inventory inventory;
    private Inventory storageView;
    private List<PlayerObserver> observers = new ArrayList<>();

    public Player(String playerName, double carryCapacity, Inventory sInventory) {
        name = playerName;
        carryWeightCapacity = carryCapacity;
        inventory = sInventory;
        storageView = sInventory; // Initialize storage view with the same inventory
    }

    public String getName() {
        return name;
    }

    public double getCarryCapacity() {
        return carryWeightCapacity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public double getCurrentWeight() {
        double carrying = 0;
        for (ItemInterface item : inventory.searchItems("")) {
            carrying += item.getWeight();
        }
        return carrying;
    }

    public void setStorageView(Inventory storageInventory) {
        storageView = storageInventory;
    }

    public Inventory getStorageView() {
        return storageView;
    }

    public void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (PlayerObserver observer : observers) {
            observer.inventoryUpdated(this);
        }
    }

    public void store(ItemInterface item) throws ItemNotAvailableException {
        // Do we have the item we are trying to store
        if (!inventory.searchItems("").contains(item)) {
            throw new ItemNotAvailableException(item.getDefinition());
        }
        storageView.addOne(inventory.remove(item));
        notifyObservers();
    }

    public void retrieve(ItemInterface item) throws ItemNotAvailableException, ExceedWeightCapacity {
        // Does the Storage have the item we are trying to retrieve
        if (!storageView.searchItems("").contains(item)) {
            throw new ItemNotAvailableException(item.getDefinition());
        }
        if (getCurrentWeight() + item.getWeight() > carryWeightCapacity) {
            throw new ExceedWeightCapacity(this, item);
        }
        inventory.addOne(storageView.remove(item));
        notifyObservers();
    }
}
