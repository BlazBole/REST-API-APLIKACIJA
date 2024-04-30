USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetInventory]    Script Date: 30. 04. 2024 09:25:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetInventory]
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Inventorys
END
