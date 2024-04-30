using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Configuration;
using REST_API_APP.Models;
using Microsoft.Extensions.Configuration;
using System.Text;
using System.Security.Cryptography;
using Microsoft.AspNetCore.Identity.Data;
using System.Data;
using REST_API_APP.DTOs;

namespace REST_API_APP.Data
{
    public class DBAccessUser
    {
        private readonly string _connectionString;

        public DBAccessUser(string connectionString)
        {
            _connectionString = connectionString;
        }

        public async Task<List<User>> GetUsersAsync()
        {
            var users = new List<User>();

            using (var connection = new SqlConnection(_connectionString))
            {
                await connection.OpenAsync();

                using (var command = new SqlCommand("[dbo].[GetUsers]", connection)) 
                {
                    command.CommandType = CommandType.StoredProcedure; 

                    using (var reader = await command.ExecuteReaderAsync())
                    {
                        while (await reader.ReadAsync())
                        {
                            users.Add(new User
                            {
                                UserId = int.Parse(reader["UserId"].ToString()),
                                UserName = reader["UserName"].ToString(),
                                Email = reader["Email"].ToString(),
                                Password = reader["Password"].ToString(),
                                Phone = reader["Phone"].ToString()
                            });
                        }
                    }
                }
            }

            return users;
        }

        public async Task<bool> UpdateUserAsync(int id, UpdateUserRequest updateUserRequest)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[UpdateUser]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@Id", id);
                command.Parameters.AddWithValue("@UserName", updateUserRequest.UserName);
                command.Parameters.AddWithValue("@Phone", updateUserRequest.Phone);
                command.Parameters.AddWithValue("@Image", updateUserRequest.Image);

                await connection.OpenAsync();


                await command.ExecuteNonQueryAsync();

                return true;


            }
        }

        public async Task<bool> DeleteUserAsync(int id)
        {
            using (var connection = new SqlConnection(_connectionString))
            {
                await connection.OpenAsync();

                using (var command = new SqlCommand("[dbo].[DeleteUserAndInventoryByUserId]", connection))
                {
                    command.CommandType = CommandType.StoredProcedure;
                    command.Parameters.AddWithValue("@UserId", id);

                    await command.ExecuteNonQueryAsync();

                    return true;
                }
            }
        }

        public async Task<User> AddUserAsync(AddUserRequest addUserRequest)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[AddUser]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@UserName", addUserRequest.UserName);
                command.Parameters.AddWithValue("@Email", addUserRequest.Email);
                command.Parameters.AddWithValue("@Password", EncryptPassword(addUserRequest.Password));
                command.Parameters.AddWithValue("@Phone", addUserRequest.Phone);
                command.Parameters.AddWithValue("@Image", addUserRequest.Image);

                await connection.OpenAsync();
                var newUserId = await command.ExecuteScalarAsync();

                var user = new User
                {
                    UserId = Convert.ToInt32(newUserId),
                    UserName = addUserRequest.UserName,
                    Email = addUserRequest.Email,
                    Password = addUserRequest.Password,
                    Phone = addUserRequest.Phone,
                    Image = addUserRequest.Image
                };

                return user;
            }
        }

        public async Task<User> GetUserAsync(int id)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[GetUser]", connection))
            {

                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@Id", id);

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    if (await reader.ReadAsync())
                    {
                        var user = new User
                        {
                            UserId = reader.GetInt32(reader.GetOrdinal("UserId")),
                            UserName = reader.GetString(reader.GetOrdinal("UserName")),
                            Email = reader.GetString(reader.GetOrdinal("Email")),
                            Password = reader.GetString(reader.GetOrdinal("Password")),
                            Phone = reader.GetString(reader.GetOrdinal("Phone")),
                            Image = reader.GetString(reader.GetOrdinal("Image"))
                        };

                        return user;
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }

        public async Task<User> GetUserByUsernameAsync(string username)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[GetUserByUsername]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;

                command.Parameters.AddWithValue("@username", username);

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    if (await reader.ReadAsync())
                    {
                        var user = new User
                        {
                            UserId = reader.GetInt32(reader.GetOrdinal("UserId")),
                            UserName = reader.GetString(reader.GetOrdinal("UserName")),
                            Email = reader.GetString(reader.GetOrdinal("Email")),
                            Password = reader.GetString(reader.GetOrdinal("Password")),
                            Phone = reader.GetString(reader.GetOrdinal("Phone")),
                            Image = reader.GetString(reader.GetOrdinal("Image"))
                        };

                        return user;
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }

        public async Task<User> GetUserByEmailAsync(string email)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[GetUserByEmail]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;

                command.Parameters.AddWithValue("@Email", email);

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    if (await reader.ReadAsync())
                    {
                        var user = new User
                        {
                            UserId = reader.GetInt32(reader.GetOrdinal("UserId")),
                            UserName = reader.GetString(reader.GetOrdinal("UserName")),
                            Email = reader.GetString(reader.GetOrdinal("Email")),
                            Password = reader.GetString(reader.GetOrdinal("Password")),
                            Phone = reader.GetString(reader.GetOrdinal("Phone")),
                            Image = reader.GetString(reader.GetOrdinal("Image"))
                        };

                        return user;
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }

        public async Task<string> LoginAsync(LoginRequest loginRequest)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[LoginUser]", connection))
            {
                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@Email", loginRequest.Email);
                command.Parameters.AddWithValue("@Password", EncryptPassword(loginRequest.Password));

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    if (await reader.ReadAsync())
                    {
                        return reader.GetString(reader.GetOrdinal("UserName"));
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }

        public async Task<User> GetUsernameByUserIdAsync(int userId)
        {
            using (var connection = new SqlConnection(_connectionString))
            using (var command = new SqlCommand("[dbo].[GetUsernameByUserId]", connection))

            {
                command.CommandType = CommandType.StoredProcedure;
                command.Parameters.AddWithValue("@UserId", userId);

                await connection.OpenAsync();

                using (var reader = await command.ExecuteReaderAsync())
                {
                    if (await reader.ReadAsync())
                    {
                        return new User
                        {
                            UserId = reader.GetInt32(reader.GetOrdinal("UserId")),
                            UserName = reader.GetString(reader.GetOrdinal("UserName")),
                            Email = reader.GetString(reader.GetOrdinal("Email")),
                            Password = reader.GetString(reader.GetOrdinal("Password")),
                            Phone = reader.GetString(reader.GetOrdinal("Phone")),
                            Image = reader.GetString(reader.GetOrdinal("Image"))
                        };
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }

        private string EncryptPassword(string password)
        {
            using (var sha256 = SHA256.Create())
            {
                var hashedBytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
                var stringBuilder = new StringBuilder();
                foreach (var b in hashedBytes)
                {
                    stringBuilder.Append(b.ToString("x2"));
                }
                return stringBuilder.ToString();
            }
        }
    }
}
