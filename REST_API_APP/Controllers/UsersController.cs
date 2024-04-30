using Microsoft.AspNetCore.Identity.Data;
using Microsoft.AspNetCore.Mvc;
using System.Security.Cryptography;
using REST_API_APP.Data;
using System.Text;
using Microsoft.Data.SqlClient;
using Microsoft.IdentityModel.Tokens;
using REST_API_APP.DTOs;

namespace REST_API_APP.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsersController : ControllerBase
    {
        private readonly DBAccessUser _dbAccess;
        private readonly DBAccessInventory _dbAccessInventory;

        public UsersController(IConfiguration configuration)
        {
            string connectionString = configuration.GetConnectionString("DefaultConnection");
            _dbAccess = new DBAccessUser(connectionString);
            _dbAccessInventory = new DBAccessInventory(connectionString);
        }

        //------------------------------------------------User------------------------------------------------

        [HttpGet]
        public async Task<IActionResult> GetUsers()
        {
            var users = await _dbAccess.GetUsersAsync();
            return Ok(users);
        }

        [HttpPut]
        [Route("{id:int}")]
        public async Task<IActionResult> UpdateUser([FromRoute] int id, UpdateUserRequest updateUserRequest)
        {
            var result = await _dbAccess.UpdateUserAsync(id, updateUserRequest);

            if (result)
            {
                return Ok("Uporabnik je bil uspešno posodobljen.");
            }
            else
            {
                return NotFound();
            }
        }

        [HttpDelete]
        [Route("{id:int}")]
        public async Task<IActionResult> DeleteUser([FromRoute] int id)
        {
            var result = await _dbAccess.DeleteUserAsync(id);

            if (result)
            {
                return Ok("Uporabnik je bil uspešno izbrisan.");
            }
            else
            {
                return NotFound();
            }
        }

        [HttpPost]
        public async Task<IActionResult> AddUser(AddUserRequest addUserRequest)
        {
            var result = await _dbAccess.AddUserAsync(addUserRequest);

            if (result != null)
            {
                return Ok(result);
            }
            else
            {
                return BadRequest("Napaka pri dodajanju uporabnika.");
            }
        }

        [HttpGet]
        [Route("{id:int}")]
        public async Task<IActionResult> GetUser([FromRoute] int id)
        {
            var user = await _dbAccess.GetUserAsync(id);

            if (user != null)
            {
                return Ok(user);
            }
            else
            {
                return NotFound();
            }
        }

        [HttpGet]
        [Route("username/{username}")]
        public async Task<IActionResult> GetUserByUsername(string username)
        {
            var user = await _dbAccess.GetUserByUsernameAsync(username);

            if (user != null)
            {
                return Ok(user);
            }
            else
            {
                return NotFound();
            }
        }

        [HttpGet]
        [Route("email/{email}")]
        public async Task<IActionResult> GetUserByEmail(string email)
        {
            var user = await _dbAccess.GetUserByEmailAsync(email);

            if (user != null)
            {
                return Ok(user);
            }
            else
            {
                return NotFound();
            }
        }

        [HttpPost]
        [Route("login")]
        public async Task<IActionResult> Login(LoginRequest loginRequest)
        {
            var result = await _dbAccess.LoginAsync(loginRequest);

            if (result != null)
            {
                return Ok(result);
            }
            else
            {
                return NotFound("Napaka pri prijavi.");
            }
        }

        [HttpGet]
        [Route("usernameById/{userId:int}")]
        public async Task<IActionResult> GetUsernameByUserId([FromRoute] int userId)
        {
            var user = await _dbAccess.GetUsernameByUserIdAsync(userId);

            if (user != null)
            {
                return Ok(user);
            }
            else
            {
                return NotFound();
            }
        }

        //------------------------------------------------INVENTORY------------------------------------------------

        [HttpPost]
        [Route("Inventory/AddToInventory")]
        public async Task<IActionResult> AddToInventory(AddToInventoryRequest addToInventoryRequest)
        {
            var result = await _dbAccessInventory.AddToInventoryAsync(addToInventoryRequest);

            if (result != null)
            {
                return Ok(result);
            }
            else
            {
                return BadRequest("Napaka pri dodajanju uporabnika.");
            }

        }

        [HttpGet]
        [Route("Inventory/GetInventory")]
        public async Task<IActionResult> GetInventory()
        {
            var inventory = await _dbAccessInventory.GetInventoryAsync();
            return Ok(inventory);
        }

        [HttpGet]
        [Route("Inventory/GetInventoryByUser/{userId}")]
        public async Task<IActionResult> GetInventoryByUser(int userId)
        {
            var inventory = await _dbAccessInventory.GetInventoryByUserIdAsync(userId);
            return Ok(inventory);
        }

        [HttpDelete]
        [Route("Inventory/DeleteInventory/{id:int}")]
        public async Task<IActionResult> DeleteInventory([FromRoute] int id)
        {
            var result = await _dbAccessInventory.DeleteInventoryAsync(id);

            if (result)
            {
                return Ok("Inventar je bil uspešno izbrisan.");
            }
            else
            {
                return NotFound();
            }
        }
    }
}
