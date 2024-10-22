using System.ComponentModel.DataAnnotations;

namespace StorageManagement.DTO
{
    public class UpdateItemDTO
    {
        [Required]
        public int id {  get; set; }
        public String? Name { get; set; }
        public int Quantity { get; set; }
    }
}
