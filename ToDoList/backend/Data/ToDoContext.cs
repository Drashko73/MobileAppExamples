using backend.Models;
using Microsoft.EntityFrameworkCore;

namespace backend.Data
{
    public class ToDoContext : DbContext
    {
        public ToDoContext(DbContextOptions options) : base(options) { }


        public DbSet<ToDoItem> ToDoItems { get; set; }
    }
}
