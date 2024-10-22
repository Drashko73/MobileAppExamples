using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("ToDoItem")]
    public class ToDoItem
    {
        [Key]
        public int Id { get; set; }
        public string Activity { get; set; } = string.Empty;
        public bool IsCompleted { get; set; }
    }
}
