using backend.Data;
using backend.DTOs;
using backend.Interfaces;
using backend.Models;
using Microsoft.EntityFrameworkCore;

namespace backend.Repositories
{
    public class ItemRepository : IItemRepository
    {
        private readonly ToDoContext _context;
        public ItemRepository(ToDoContext context)
        {
            _context = context;
        }
        public async Task<ToDoItem> CreateToDoItem(CreateItemDto item)
        {
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.Activity = item.Activity;
            toDoItem.IsCompleted = false;

            await _context.AddAsync(toDoItem);
            await _context.SaveChangesAsync();

            return toDoItem;
        }

        public async Task<ToDoItem?> DeleteToDoItem(int id)
        {
            var item = _context.ToDoItems.FirstOrDefault(i => i.Id == id);
            if (item != null)
            {
                _context.ToDoItems.Remove(item);
            }
            await _context.SaveChangesAsync();
            return item;
        }

        public async Task<List<ToDoItem>> GetAllItems()
        {
            return await _context.ToDoItems.ToListAsync();
        }

        public async Task<ToDoItem?> UpdateToDoItem(UpdateItemDto updateItemDto)
        {
            var item = await _context.ToDoItems.FirstOrDefaultAsync(i=>i.Id == updateItemDto.Id);
            if (item != null)
            {
                item.IsCompleted = updateItemDto.IsCompleted;
            }
            await _context.SaveChangesAsync();
            return item;
        }
    }
}
