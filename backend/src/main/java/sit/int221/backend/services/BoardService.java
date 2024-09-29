package sit.int221.backend.services;

import io.viascom.nanoid.NanoId;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import sit.int221.backend.dtos.AddEditBoardDTO;
import sit.int221.backend.dtos.BoardDTO;
import sit.int221.backend.dtos.BoardOwnerDTO;
import sit.int221.backend.dtos.VisibilityDTO;
import sit.int221.backend.exceptions.BoardAccessDeniedException;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.project_management.Board;
import sit.int221.backend.project_management.BoardRepository;
import sit.int221.backend.project_management.Visibility;
import sit.int221.backend.user_account.User;

import java.util.*;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
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
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("Board with ID '" + boardId + "' not found"));

        if (!board.getOwnerId().equals(userId) && board.getVisibility() != Visibility.PUBLIC) {
            throw new BoardAccessDeniedException("User does not have permission to view this board");
        }

        return convertToBoardDTO(board, owner);
    }

    public Optional<Board> getBoardById(String boardId) {
        return boardRepository.findById(boardId);
    }

    private BoardDTO convertToBoardDTO(Board board, User owner) {
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

        if (owner != null) {
            BoardOwnerDTO boardOwnerDTO = modelMapper.map(owner, BoardOwnerDTO.class);
            boardDTO.setOwner(Optional.of(boardOwnerDTO));
        } else {
            boardDTO.setOwner(Optional.empty());
        }

        return boardDTO;
    }

    public BoardDTO createBoard(AddEditBoardDTO boardDTO, String userId) {
        User currentUser = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Board newBoard = modelMapper.map(boardDTO, Board.class);
        newBoard.setId(NanoId.generate(10));
        newBoard.setOwnerId(userId);
        Board savedBoard = boardRepository.save(newBoard);
        statusService.createDefaultStatuses(savedBoard);
        return convertToBoardDTO(savedBoard, currentUser);
    }

    public BoardDTO updateBoardVisibility(String userId, String boardId, VisibilityDTO visibilityDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("Board with ID '" + boardId + "' not found"));

        if (!board.getOwnerId().equals(userId)) {
            throw new BoardAccessDeniedException("User does not have permission to view this board");
        }

        if (visibilityDTO == null || !isValidVisibility(visibilityDTO.getVisibility())) {
            throw new HttpMessageNotReadableException("Visibility must be either PRIVATE or PUBLIC");
        }

        board.setVisibility(visibilityDTO.getVisibility());
        Board updatedBoard = boardRepository.save(board);

        User owner = userService.getUserById(updatedBoard.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return convertToBoardDTO(updatedBoard, owner);
    }

    private boolean isValidVisibility(Visibility visibility) {
        return visibility == Visibility.PRIVATE || visibility == Visibility.PUBLIC;
    }

    public BoardDTO getPublicBoardById(String boardId) {
        Board board = getBoardById(boardId)
                .orElseThrow(() -> new NotFoundException("Board with ID '" + boardId + "' not found"));

        if (board.getVisibility() != Visibility.PUBLIC) {
               throw  new BoardAccessDeniedException("User does not have permission to view this board");
        }

        User owner = userService.getUserById(board.getOwnerId()).orElse(null);
        return convertToBoardDTO(board, owner);
    }
}



