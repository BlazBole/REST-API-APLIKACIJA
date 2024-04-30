using Microsoft.Data.SqlClient;
using REST_API_APP.DTOs;
using REST_API_APP.Models;
using System.Data;

namespace REST_API_APP.Data
{
    public class DBAccessInventory
    {
        private readonly string _connectionString;

        public DBAccessInventory(string connectionString)
        {
            _connectionString = connectionString;
        }

        public async Task<Inventory> AddToInventoryAsync(AddToInventoryRequest addToInventoryRequest)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[AddInventory]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@InventoryNumber", addToInventoryRequest.InventoryNumber);
                command.Parameters.AddWithValue("@InventoryName", addToInventoryRequest.InventoryName);
                command.Parameters.AddWithValue("@EntryDate", addToInventoryRequest.EntryDate);
                command.Parameters.AddWithValue("@LocationRoom", addToInventoryRequest.LocationRoom);
                command.Parameters.AddWithValue("@UserId", addToInventoryRequest.UserId);

                await connection.OpenAsync();

                var newInventoryId = await command.ExecuteScalarAsync();

                if (newInventoryId != null && int.TryParse(newInventoryId.ToString(), out int inventoryId))
                {
                    var inventory = new Inventory
                    {
                        Id = inventoryId,
                        InventoryNumber = addToInventoryRequest.InventoryNumber,
                        InventoryName = addToInventoryRequest.InventoryName,
                        EntryDate = addToInventoryRequest.EntryDate,
                        LocationRoom = addToInventoryRequest.LocationRoom,
                        UserId = addToInventoryRequest.UserId
                    };

                    return inventory;
                }
                else
                {
                    return null;
                }
            }
        }

        public async Task<List<Inventory>> GetInventoryAsync()
        {
            var inventorys = new List<Inventory>();

            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[GetInventory]", connection)) 
            {
                command.CommandType = CommandType.StoredProcedure; 

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    while (await reader.ReadAsync())
                    {
                        inventorys.Add(new Inventory
                        {
                            Id = int.Parse(reader["Id"].ToString()),
                            InventoryNumber = reader["InventoryNumber"].ToString(),
                            InventoryName = reader["InventoryName"].ToString(),
                            EntryDate = reader["EntryDate"].ToString(),
                            LocationRoom = reader["LocationRoom"].ToString(),
                            UserId = reader.GetInt32(reader.GetOrdinal("UserId"))
                        });
                    }
                }
            }

            return inventorys;
        }

        public async Task<List<Inventory>> GetInventoryByUserIdAsync(int userId)
        {
            var inventorys = new List<Inventory>();

            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[GetInventoryByUserId]", connection)) 
            {
                command.CommandType = CommandType.StoredProcedure; 

                command.Parameters.AddWithValue("@UserId", userId); 

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    while (await reader.ReadAsync())
                    {
                        inventorys.Add(new Inventory
                        {
                            Id = reader.GetInt32(reader.GetOrdinal("Id")),
                            InventoryNumber = reader["InventoryNumber"].ToString(),
                            InventoryName = reader["InventoryName"].ToString(),
                            EntryDate = reader["EntryDate"].ToString(),
                            LocationRoom = reader["LocationRoom"].ToString(),
                            UserId = reader.GetInt32(reader.GetOrdinal("UserId"))
                        });
                    }
                }
            }

            return inventorys;
        }

        public async Task<bool> DeleteInventoryAsync(int id)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[DeleteInventory]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@Id", id);

                await connection.OpenAsync();

                await command.ExecuteNonQueryAsync();

                return true;
            }
        }
    }
}
