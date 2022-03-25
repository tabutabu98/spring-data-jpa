package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.domain.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() throws Exception {
        // given
        Item item = new Item("item_1");
        itemRepository.save(item);

        // when
//        Optional<Item> findItem = itemRepotitory.findById(item.getId());

        // then

    }
}