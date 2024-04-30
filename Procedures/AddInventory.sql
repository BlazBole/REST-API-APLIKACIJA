USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[AddInventory]    Script Date: 30. 04. 2024 09:10:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[AddInventory]
	@InventoryNumber NVARCHAR(100),
    @InventoryName NVARCHAR(100),
    @EntryDate NVARCHAR(100),
    @LocationRoom NVARCHAR(100),
    @UserId INT
AS
BEGIN
	SET NOCOUNT ON;

    INSERT INTO Inventorys (InventoryNumber, InventoryName, EntryDate, LocationRoom, UserId)
    VALUES (@InventoryNumber, @InventoryName, @EntryDate, @LocationRoom, @UserId);

    SELECT SCOPE_IDENTITY() AS InventoryId, @InventoryNumber AS InventoryNumber, @InventoryName AS InventoryName, @EntryDate AS EntryDate, @LocationRoom AS LocationRoom, @UserId AS UserId;
END
