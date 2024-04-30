namespace REST_API_APP.DTOs
{
    public class AddToInventoryRequest
    {
        public string InventoryNumber { get; set; }
        public string InventoryName { get; set; }
        public string EntryDate { get; set; }
        public string LocationRoom { get; set; }
        public int UserId { get; set; }
    }
}
