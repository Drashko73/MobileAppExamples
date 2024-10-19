using backend.Data;
using backend.DTOs;
using backend.Interfaces;
using backend.Models;

namespace backend.Repositories
{
    public class ItemRepository : IItemRepository
    {
        private readonly ToDoContext _context;
        public ItemRepository(ToDoContext context)
        {
            _context = context;
        }
        public void CreateToDoItem(CreateItemDto item)
        {
            throw new NotImplementedException();
        }

        public void DeleteToDoItem(int id)
        {
            throw new NotImplementedException();
        }

        public List<ToDoItem> GetAllItems()
        {
            throw new NotImplementedException();
        }

        public void UpdateToDoItem(UpdateItemDto updateItemDto)
        {
            throw new NotImplementedException();
        }
    }
}
