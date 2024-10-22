using System.ComponentModel.DataAnnotations;

namespace StorageManagement.DTO
{
    public class AddItemDTO
    {
        [Required]
        [MinLength(2)]
        public String Name { get; set; }
        public int Quantity {  get; set; } = 0;
    }
}
