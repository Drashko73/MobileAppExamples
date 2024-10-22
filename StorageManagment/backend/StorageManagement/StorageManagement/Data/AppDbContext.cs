using Microsoft.EntityFrameworkCore;
using StorageManagement.Models;

namespace StorageManagement.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<StorageItem> StorageItems { get; set; }
    }
}
