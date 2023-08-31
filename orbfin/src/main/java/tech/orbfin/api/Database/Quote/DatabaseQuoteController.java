package tech.orbfin.api.Database.Quote;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseQuoteController {

  @Autowired
  private DatabaseQuoteRepository quoteRepository;

  public List<DatabaseQuote> getAllQuotes() {
    return quoteRepository.findAll();
  }

  public DatabaseQuote getQuoteById(Long id) {
    return quoteRepository.findById(id).get();
  }

  public DatabaseQuote createQuote(DatabaseQuote quote) {

    return quoteRepository.save(quote);
  }

  public DatabaseQuote updateQuote(Long id, DatabaseQuote quote) {
    DatabaseQuote existingQuote = quoteRepository.findById(id).get();
    existingQuote.setItems(quote.getItems());
    existingQuote.setDescription(quote.getDescription());
    return quoteRepository.save(existingQuote);
  }

  public String deleteQuote(Long id) {
    try {
      quoteRepository.findById(id).get();
      quoteRepository.deleteById(id);
      return "Quote deleted successfully";
    } catch (Exception e) {
      return "Quote not found in the database: " + e.getMessage();
    }
  }
}