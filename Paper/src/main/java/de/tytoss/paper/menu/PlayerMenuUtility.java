package de.tytoss.paper.menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PlayerMenuUtility {

    private final Player owner;
    private final Map<String, Object> dataMap = new HashMap<>();
    private final Stack<Menu> history = new Stack<>();

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setData(String identifier, Object data) {
        dataMap.put(identifier, data);
    }

    public void setData(Enum<?> identifier, Object data) {
        dataMap.put(identifier.toString(), data);
    }

    public Object getData(String identifier) {
        return dataMap.get(identifier);
    }

    public Object getData(Enum<?> identifier) {
        return dataMap.get(identifier.toString());
    }

    public <T> T getData(String identifier, Class<T> classRef) {
        Object obj = dataMap.get(identifier);
        return obj != null ? classRef.cast(obj) : null;
    }

    public <T> T getData(Enum<?> identifier, Class<T> classRef) {
        Object obj = dataMap.get(identifier.toString());
        return obj != null ? classRef.cast(obj) : null;
    }

    public Menu lastMenu() {
        history.pop();
        return history.pop();
    }

    public void pushMenu(Menu menu) {
        history.push(menu);
    }
}
