# GraphQL Bookstore

A Spring Boot GraphQL Proof of Concept (POC) for a complete online book store using MySQL database.

## Features

- **Spring Boot** with **GraphQL Java Tools**
- **MySQL Database**
- **GraphQL schema-first approach** (`.graphqls` file)
- **CRUD operations** for all entities:
  - Users
  - Books
  - Categories
  - Orders
  - Order Items
- **ZonedDateTime** scalar support via `graphql-java-extended-scalars`
- GraphQL endpoint exposed at `/graphql`

## Entities

### User
- Represents a customer of the bookstore
- Can place orders
- Has authentication capabilities

### Book
- Represents a book in the store
- Belongs to a category
- Has stock quantity tracking

### Category
- Represents a book category
- Contains multiple books

### Order
- Represents a customer order
- Contains order items
- Has status tracking

### OrderItem
- Represents an item in an order
- Links to a book and quantity

## Sample GraphQL Queries

### User Queries

```graphql
# Get all users
query {
  users {
    id
    name
    email
    createdAt
    updatedAt
  }
}

# Get user by ID
query {
  user(id: 1) {
    id
    name
    email
    address
    phoneNumber
    orders {
      id
      status
      totalAmount
    }
    createdAt
    updatedAt
  }
}

# Get user by email
query {
  userByEmail(email: "john.doe@example.com") {
    id
    name
    email
    createdAt
    updatedAt
  }
}
```

### Book Queries

```graphql
# Get all books
query {
  books {
    id
    title
    author
    isbn
    price
    stockQuantity
    category {
      name
    }
    createdAt
    updatedAt
  }
}

# Get book by ID
query {
  book(id: 1) {
    id
    title
    author
    description
    isbn
    price
    stockQuantity
    category {
      id
      name
    }
    publisher
    publicationYear
    language
    pageCount
    createdAt
    updatedAt
  }
}

# Get book by ISBN
query {
  bookByIsbn(isbn: "978-1234567890") {
    id
    title
    author
    price
  }
}

# Get books by title (partial match)
query {
  booksByTitle(title: "Great") {
    id
    title
    author
    price
  }
}

# Get books by author (partial match)
query {
  booksByAuthor(author: "Jane") {
    id
    title
    author
    price
  }
}

# Get books by category
query {
  booksByCategory(categoryId: 1) {
    id
    title
    author
    price
  }
}
```

### Category Queries

```graphql
# Get all categories
query {
  categories {
    id
    name
    description
    books {
      id
      title
    }
    createdAt
    updatedAt
  }
}

# Get category by ID
query {
  category(id: 1) {
    id
    name
    description
    books {
      id
      title
      author
    }
    createdAt
    updatedAt
  }
}

# Get category by name
query {
  categoryByName(name: "Fiction") {
    id
    name
    description
    books {
      id
      title
    }
  }
}
```

### Order Queries

```graphql
# Get all orders
query {
  orders {
    id
    user {
      name
    }
    status
    totalAmount
    createdAt
    updatedAt
  }
}

# Get order by ID
query {
  order(id: 1) {
    id
    user {
      id
      name
      email
    }
    status
    totalAmount
    orderItems {
      id
      book {
        title
        author
      }
      quantity
      price
    }
    shippingAddress
    billingAddress
    paymentMethod
    trackingNumber
    createdAt
    updatedAt
  }
}

# Get orders by user
query {
  ordersByUser(userId: 1) {
    id
    status
    totalAmount
    createdAt
  }
}

# Get orders by status
query {
  ordersByStatus(status: PROCESSING) {
    id
    user {
      name
    }
    totalAmount
    createdAt
  }
}
```

## Sample GraphQL Mutations

### User Mutations

```graphql
# Create a new user
mutation {
  createUser(input: {
    name: "Alice Brown"
    email: "alice.brown@example.com"
    password: "password123"
    address: "321 Elm St, Somewhere, USA"
    phoneNumber: "555-111-2222"
  }) {
    id
    name
    email
    createdAt
  }
}

# Update an existing user
mutation {
  updateUser(input: {
    id: 1
    name: "John Doe Jr."
    address: "456 New Address St, Anytown, USA"
  }) {
    id
    name
    email
    address
    updatedAt
  }
}

# Delete a user
mutation {
  deleteUser(id: 3)
}

# Authenticate a user
mutation {
  authenticateUser(email: "john.doe@example.com", password: "password123") {
    id
    name
    email
  }
}
```

### Book Mutations

```graphql
# Create a new book
mutation {
  createBook(input: {
    title: "New Book Title"
    author: "New Author"
    description: "A brand new book"
    isbn: "978-1111111111"
    price: 18.99
    stockQuantity: 20
    categoryId: 1
    publisher: "New Publisher"
    publicationYear: 2023
    language: "English"
    pageCount: 300
  }) {
    id
    title
    author
    isbn
    createdAt
  }
}

# Update an existing book
mutation {
  updateBook(input: {
    id: 1
    price: 21.99
    stockQuantity: 45
    description: "Updated description for this book"
  }) {
    id
    title
    price
    stockQuantity
    description
    updatedAt
  }
}

# Delete a book
mutation {
  deleteBook(id: 5)
}

# Update book stock
mutation {
  updateBookStock(id: 1, quantity: 10) {
    id
    title
    stockQuantity
    updatedAt
  }
}
```

### Category Mutations

```graphql
# Create a new category
mutation {
  createCategory(input: {
    name: "Children's Books"
    description: "Books for children and young readers"
  }) {
    id
    name
    description
    createdAt
  }
}

# Update an existing category
mutation {
  updateCategory(input: {
    id: 1
    description: "Updated description for fiction category"
  }) {
    id
    name
    description
    updatedAt
  }
}

# Delete a category
mutation {
  deleteCategory(id: 5)
}
```

### Order Mutations

```graphql
# Create a new order
mutation {
  createOrder(input: {
    userId: 1
    orderItems: [
      {
        bookId: 1
        quantity: 2
      },
      {
        bookId: 3
        quantity: 1
      }
    ]
    shippingAddress: "123 Main St, Anytown, USA"
    billingAddress: "123 Main St, Anytown, USA"
    paymentMethod: "Credit Card"
  }) {
    id
    user {
      name
    }
    status
    totalAmount
    orderItems {
      book {
        title
      }
      quantity
      price
    }
    createdAt
  }
}

# Update order status
mutation {
  updateOrderStatus(input: {
    id: 1
    status: SHIPPED
  }) {
    id
    status
    updatedAt
  }
}

# Update order shipping information
mutation {
  updateOrderShipping(input: {
    id: 1
    trackingNumber: "TRK987654321"
  }) {
    id
    trackingNumber
    updatedAt
  }
}

# Add item to order
mutation {
  addOrderItem(input: {
    orderId: 1
    bookId: 4
    quantity: 1
  }) {
    id
    totalAmount
    orderItems {
      book {
        title
      }
      quantity
      price
    }
    updatedAt
  }
}

# Remove item from order
mutation {
  removeOrderItem(input: {
    orderId: 1
    orderItemId: 3
  }) {
    id
    totalAmount
    orderItems {
      id
      book {
        title
      }
      quantity
    }
    updatedAt
  }
}

# Cancel order
mutation {
  cancelOrder(id: 2) {
    id
    status
    updatedAt
  }
}

# Delete order
mutation {
  deleteOrder(id: 3)
}
```

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Ensure MySQL is running on localhost:3306
   - Create a database named `bookstoredb` or ensure your MySQL user has permissions to create databases
   - Default username: `root`
   - Default password: `root`
   - You can modify these settings in `application.properties` if needed
4. Run the application:
   ```
   cd graphql-bookstore
   mvn spring-boot:run
   ```
5. Access the GraphiQL interface at: http://localhost:8080/graphiql
6. Use the sample queries and mutations provided above to interact with the API

## Technologies Used

- Spring Boot
- Spring Data JPA
- MySQL Database
- GraphQL Java
- GraphQL Java Tools
- GraphQL Java Extended Scalars
- Lombok
