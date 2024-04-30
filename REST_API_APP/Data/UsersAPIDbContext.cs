using Microsoft.EntityFrameworkCore;
using REST_API_APP.Models;

namespace REST_API_APP.Data
{
    public class UsersAPIDbContext : DbContext
    {
        public UsersAPIDbContext(DbContextOptions options) : base(options)
        {
        }

        public DbSet<User> Users { get; set; }
    }
}