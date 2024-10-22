using backend.DTOs;
using backend.Interfaces;
using backend.Models;
using Microsoft.AspNetCore.Mvc;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ToDoController : Controller
    {
        private readonly IItemRepository _itemRepo;
        public ToDoController(IItemRepository itemRepo)
        {
            _itemRepo = itemRepo;
        }


        [HttpGet("GetAllItems")]
        public async Task<IActionResult> GetAllItems()
        {
            return Ok(await _itemRepo.GetAllItems());
        }

        [HttpPost("CreateItem")]

        public async Task<IActionResult> CreateItem(CreateItemDto dto)
        {
            var item = await _itemRepo.CreateToDoItem(dto);
            if (item == null)
                return BadRequest("Creation failed");
            return Ok(item);
        }


        [HttpDelete("DeleteItem/{id}")]
        public async Task<IActionResult> DeleteItembyId(int id)
        {
            var item = await _itemRepo.DeleteToDoItem(id);
            if (item == null)
                return BadRequest("Deletition failed");
            return Ok("Item deleted");
        }

        [HttpPut("UpdateItem")]
        public async Task<IActionResult> UpdateItem(UpdateItemDto dto)
        {
            var item = await _itemRepo.UpdateToDoItem(dto);
            if (item == null)
                return BadRequest("Update failed");
            return Ok(item);
        }
    }
}
