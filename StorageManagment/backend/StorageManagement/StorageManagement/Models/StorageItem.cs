using System.ComponentModel.DataAnnotations;

namespace StorageManagement.Models
{
    public class StorageItem
    {
        [Required]
        public int Id { get; set; }
        [Required]
        public string Name { get; set; } = string.Empty;
        public int Quantity { get; set; } = 0;
    }
}
