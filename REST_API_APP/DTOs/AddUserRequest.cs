namespace REST_API_APP.DTOs
{
    public class AddUserRequest
    {
        public string UserName { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string Phone { get; set; }
        public string Image { get; set; }
    }
}
