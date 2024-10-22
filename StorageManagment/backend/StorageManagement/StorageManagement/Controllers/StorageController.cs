using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StorageManagement.Data;
using StorageManagement.DTO;
using StorageManagement.Models;

namespace StorageManagement.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class StorageController : ControllerBase
    {
        private readonly AppDbContext _context;

        public StorageController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("GetAllStorageItems")]
        public async Task<List<StorageItem>> GetAllStorageItems()
        {
            return await _context.StorageItems.ToListAsync();
        }

        [HttpGet("GetStorageItemById/{id}")]
        public async Task<StorageItem> GetAllStorageItemById(int id)
        {
            var item = await _context.StorageItems.FindAsync(id);
            return item;
        }

        [HttpPost("AddStorageItem")]
        public async Task<String> AddStorageItem(AddItemDTO itemDTO)
        {
            var newItem = new StorageItem();
            newItem.Name = itemDTO.Name;
            newItem.Quantity = itemDTO.Quantity;

            await _context.AddAsync(newItem);
            await _context.SaveChangesAsync();

            return "Item " + newItem.Name + " added successfully";
        }

        [HttpDelete("DeleteStorageItem/{id}")]
        public async Task<String> DeleteStorageItem(int id)
        {
            var itemToRemove = await _context.StorageItems.FindAsync(id);

            if (itemToRemove != null)
            {
                _context.Remove(itemToRemove);
                await _context.SaveChangesAsync();
                return "Item " + itemToRemove.Name + " deleted successfully"; 
            }

            return "Item doesn't exist";
        }

        [HttpPost("UpdateStorageItem")]
        public async Task<String> UpdateStorageItem(UpdateItemDTO dto)
        {
            var itemToUpdate = await _context.StorageItems.FindAsync(dto.id);

            if (itemToUpdate != null)
            {
                if (dto.Name!= null && itemToUpdate.Name != dto.Name)
                {
                    itemToUpdate.Name = dto.Name;
                }

                if (itemToUpdate.Quantity != dto.Quantity)
                {
                    itemToUpdate.Quantity = dto.Quantity;
                }

                await _context.SaveChangesAsync();
                return "Item with id " + itemToUpdate.Id + " updated successfully";
            }

            return "Item with given id doesn't exist";
        }

    }
}
