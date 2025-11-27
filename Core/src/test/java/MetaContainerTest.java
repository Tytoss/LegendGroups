import de.tytoss.core.metadata.MetaContainer;
import de.tytoss.core.metadata.MetaData;
import org.junit.jupiter.api.Test;

import java.util.List;

class MetaContainerTest {

    @Test
    void testAddAndGetMeta() {
        MetaContainer container = new MetaContainer();
        MetaData<String> meta = new MetaData<>("key1", "value1");
        container.addMeta(meta);

        List<MetaData<String>> metas = container.getMeta("key1");
        assert metas.size() == 1;
        assert metas.getFirst().getValue().equals("value1");
    }

    @Test
    void testRemoveMeta() {
        MetaContainer container = new MetaContainer();
        MetaData<String> meta = new MetaData<>("key1", "value1");
        container.addMeta(meta);

        container.removeMeta("key1");
        List<MetaData<String>> metas = container.getMeta("key1");
        assert metas.isEmpty();
    }

    @Test
    void testCleanupExpired() throws InterruptedException {
        MetaContainer container = new MetaContainer();
        MetaData<String> meta = new MetaData<>("key1", "value1", System.currentTimeMillis() + 100);
        container.addMeta(meta);

        assert !container.getMeta("key1").isEmpty();
        Thread.sleep(150);
        container.cleanupExpired();

        assert container.getMeta("key1").isEmpty();
    }

    @Test
    void testGetFirstMeta() {
        MetaContainer container = new MetaContainer();
        container.addMeta(new MetaData<>("key1", "first"));
        container.addMeta(new MetaData<>("key1", "second"));

        MetaData<?> first = container.getMeta("key1").getFirst();
        assert first != null;
        assert first.getValue().equals("first");
    }
}

