using backend.DTOs;
using backend.Models;
using Microsoft.EntityFrameworkCore.Update.Internal;

namespace backend.Interfaces
{
    public interface IItemRepository
    {
        public Task<ToDoItem> CreateToDoItem(CreateItemDto item);
        public Task<ToDoItem?> UpdateToDoItem(UpdateItemDto updateItemDto);

        public Task<ToDoItem?> DeleteToDoItem(int id);
        public Task<List<ToDoItem>> GetAllItems();
    }
}
