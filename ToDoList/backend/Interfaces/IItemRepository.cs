using backend.DTOs;
using backend.Models;
using Microsoft.EntityFrameworkCore.Update.Internal;

namespace backend.Interfaces
{
    public interface IItemRepository
    {
        public void CreateToDoItem(CreateItemDto item);
        public void UpdateToDoItem(UpdateItemDto updateItemDto);

        public void DeleteToDoItem(int id);
        public List<ToDoItem> GetAllItems();
    }
}
