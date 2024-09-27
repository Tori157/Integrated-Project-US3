package sit.int221.backend.services;

import io.viascom.nanoid.NanoId;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import sit.int221.backend.dtos.AddEditBoardDTO;
import sit.int221.backend.dtos.BoardDTO;
import sit.int221.backend.dtos.BoardOwnerDTO;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.project_management.Board;
import sit.int221.backend.project_management.BoardRepository;
import sit.int221.backend.project_management.Visibility;
import sit.int221.backend.user_account.User;
import sit.int221.backend.utils.BoardServiceUtil;

import java.util.*;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardServiceUtil boardServiceUtil;
    private final StatusService statusService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public List<BoardDTO> getAllBoardsByUserId(String userId) {
        List<Board> allBoards = boardRepository.findAllByOwnerId(userId);
        User owner = userService.getUserById(userId).orElse(null);
        return allBoards.stream().map(board -> convertToBoardDTO(board, owner)).toList();
    }

    public BoardDTO getBoardByUserAndId(String userId, String boardId) {
        User owner = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return boardRepository.findByIdAndOwnerId(boardId, userId)
                .map(board -> convertToBoardDTO(board, owner))
                .orElseThrow(() -> new NotFoundException("Board id '" + boardId + "' not found"));
    }

    public Optional<Board> getBoardById(String boardId) {
        return boardRepository.findById(boardId);
    }

    private BoardDTO convertToBoardDTO(Board board, User owner) {
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        boardDTO.setOwner(Optional.ofNullable(owner).map(user -> modelMapper.map(user, BoardOwnerDTO.class)));
        return boardDTO;
    }

    public BoardDTO createBoard(AddEditBoardDTO boardDto, String userId) {
        User currentUser = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Board newBoard = Board.builder().id(NanoId.generate(10)).name(boardDto.getName()).ownerId(userId).build();
        if (boardDto.getVisibility() != null) {
            newBoard.setVisibility(boardDto.getVisibility());
        }
        Board savedBoard = boardRepository.save(newBoard);
        statusService.createDefaultStatuses(savedBoard);
        return convertToBoardDTO(savedBoard, currentUser);
    }

    public BoardDTO updateBoardVisibility(String boardId, Visibility visibility) {
        Board board = getBoardById(boardId).orElseThrow(() -> new NotFoundException("Board not found"));
        board.setVisibility(visibility);
        Board updatedBoard = boardRepository.save(board);
        return convertToBoardDTO(updatedBoard, userService.getUserById(updatedBoard.getOwnerId()).orElse(null));
    }

}



