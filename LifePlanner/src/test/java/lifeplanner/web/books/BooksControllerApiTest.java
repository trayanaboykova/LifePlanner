package lifeplanner.web.books;

import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BooksController.class)
class BooksControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final UUID bookId = UUID.randomUUID();
    private final User testUser = new User();
    private final Book testBook = new Book();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(userId, "user", "password", UserRole.USER, true);
    }

    @Test
    void getMyBooksPage_ShouldReturnViewWithBooks() throws Exception {
        testUser.setId(userId);
        testBook.setId(bookId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(bookService.getBooksByUser(testUser)).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/books/my-books").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("my-books"))
                .andExpect(model().attribute("pageTitle", "My Books"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("books", Arrays.asList(testBook)));
    }

    @Test
    void getAllBooksPage_ShouldReturnViewWithBooks() throws Exception {
        testUser.setId(userId);
        testBook.setId(bookId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(bookService.getBooksByUser(testUser)).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/books/all-books").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-books"))
                .andExpect(model().attribute("pageTitle", "All Books"))
                .andExpect(model().attribute("books", Arrays.asList(testBook)));
    }

    @Test
    void showAddBookRequest_ShouldReturnAddBookView() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/books/new").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-books"))
                .andExpect(model().attributeExists("pageTitle", "user", "addBookRequest"));
    }

    @Test
    void addBook_WithValidRequest_ShouldRedirect() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/books")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("bookStatus", "READ")
                        .param("title", "Test Book")
                        .param("author", "Test Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/all-books"));
    }

    @Test
    void addBook_WithInvalidRequest_ShouldReturnForm() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/books")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("title", "")) // empty title likely triggers an error
                .andExpect(status().isOk())
                .andExpect(view().name("add-books"));
    }

    @Test
    void showEditBookRequest_ShouldReturnEditView() throws Exception {
        testBook.setId(bookId);
        when(bookService.getBookById(bookId)).thenReturn(testBook);

        mockMvc.perform(get("/books/{id}/edit", bookId).with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-book"))
                .andExpect(model().attributeExists("pageTitle", "book", "editBookRequest"));
    }

    @Test
    void updateBook_WithValidRequest_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/books/{id}/edit", bookId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("bookStatus", "READ")
                        .param("title", "Updated Title")
                        .param("author", "Updated Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/all-books"));
    }

    @Test
    void updateBook_WithBindingErrors_ShouldReturnEditView() throws Exception {
        testBook.setId(bookId);
        when(bookService.getBookById(bookId)).thenReturn(testBook);

        mockMvc.perform(post("/books/{id}/edit", bookId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("bookStatus", "READ")
                        .param("title", "")  // empty title triggers validation error
                        .param("author", "Some Author"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-book"))
                .andExpect(model().attributeExists("book", "editBookRequest"));
    }

    @Test
    void getAllReadPage_ShouldReturnReadBooksView() throws Exception {
        testUser.setId(userId);
        testBook.setId(bookId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(bookService.getBooksByUser(testUser)).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/books/read-books").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("read-books"))
                .andExpect(model().attribute("pageTitle", "Read Books"))
                .andExpect(model().attribute("books", Arrays.asList(testBook)));
    }

    @Test
    void getWishListPage_ShouldReturnWishListView() throws Exception {
        testUser.setId(userId);
        testBook.setId(bookId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(bookService.getBooksByUser(testUser)).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/books/wished-books").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("wished-books"))
                .andExpect(model().attribute("pageTitle", "Wish List"))
                .andExpect(model().attribute("books", Arrays.asList(testBook)));
    }

    @Test
    void shareBook_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/books/{id}/share", bookId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/my-books"));
    }

    @Test
    void removeSharing_ShouldRedirectToMySharedPosts() throws Exception {
        mockMvc.perform(post("/books/{id}/remove", bookId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-shared-posts"));
    }

    @Test
    void deleteBook_ShouldRedirect() throws Exception {
        mockMvc.perform(delete("/books/{id}", bookId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/my-books"));
    }
}