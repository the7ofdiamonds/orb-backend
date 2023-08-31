package tech.orbfin.api.Database.Item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseItemController {

  @Autowired
  private DatabaseItemRepository itemRepository;

  public List<DatabaseItem> getAllItems() {
    return itemRepository.findAll();
  }

  public DatabaseItem getItemById(Long id) {
    return itemRepository.findById(id).get();
  }

  public DatabaseItem createItem(DatabaseItem item) {

    return itemRepository.save(item);
  }

//   public DatabaseItem updateItem(Long id, DatabaseItem item) {
//     DatabaseItem existingItem = itemRepository.findById(id).get();
//     existingItem.setItems(item.getItems());
//     return itemRepository.save(existingItem);
//   }

  public String deleteItem(Long id) {
    try {
      itemRepository.findById(id).get();
      itemRepository.deleteById(id);
      return "Item deleted successfully";
    } catch (Exception e) {
      return "Item not found in the database: " + e.getMessage();
    }
  }
}