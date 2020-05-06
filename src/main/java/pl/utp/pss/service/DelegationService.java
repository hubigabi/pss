package pl.utp.pss.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.DelegationRepository;
import pl.utp.pss.repository.UserRepository;;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DelegationService {

    DelegationRepository delegationRepository;
    UserRepository userRepository;

    @Autowired
    public DelegationService(DelegationRepository delegationRepository, UserRepository userRepository) {
        this.delegationRepository = delegationRepository;
        this.userRepository = userRepository;
    }

    public List<Delegation> getAllDelegations() {
        return delegationRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    public List<Delegation> getAllByUserId(long id) {
        return delegationRepository.findAllByUser(userRepository.findById(id).get());
    }

    public Delegation getDelegation(long id) {
        return delegationRepository.findById(id).get();
    }

    public Delegation createDelegation(Delegation delegation) {
        return delegationRepository.save(delegation);
    }

    public Delegation updateDelegation(Delegation delegation) {
        return delegationRepository.save(delegation);
    }

    public void deleteDelegation(long userId, long delegationId) {
        Delegation delegation = delegationRepository.findById(delegationId).get();

        User user = userRepository.findById(userId).get();
        user.getDelegations().remove(delegation);
        userRepository.save(user);

        delegationRepository.deleteById(delegationId);
    }

    public void deleteEmptyDelegation(Delegation delegation) {
        delegationRepository.delete(delegation);
    }

    public String generateReport(long delegationId) {
        Delegation del = delegationRepository.findById(delegationId).get();
        User user = del.getUser();

        try {
            String pathToPdf = "reports/" + delegationId + ".pdf";

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pathToPdf));
            document.open();

            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, BaseColor.BLACK);

            Paragraph paragraph = new Paragraph("Delegation\n\n", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);

            PdfPTable table = new PdfPTable(2);

            Stream.of("Name", user.getName() + " " + user.getLastName())
                    .forEach(s -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(s));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(header);
                    });


            List<PdfPCell> keyPdfPCells = Stream.of("Description", "Start date", "Stop date", "Travel diet amount",
                    "Number of breakfast", "Number of dinners", "Number of suppers",
                    "Transport type", "Ticket price", "Auto capacity", "Km",
                    "Accommodation price", "Other outlay desc", "Other outlay price")
                    .map(s -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(s));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        return cell;
                    })
                    .collect(Collectors.toList());

            List<PdfPCell> valuePdfPCells = Stream.of(del.getDescription(), del.getDateTimeStart().toString(),
                    del.getDateTimeStop().toString(), del.getTravelDietAmount() + "", del.getBreakfastNumber() + "",
                    del.getDinnerNumber() + "", del.getSupperNumber() + "", del.getTransportType().toString(),
                    del.getTicketPrice() + "", del.getAutoCapacity().toString(), del.getKm() + "",
                    del.getAccommodationPrice() + "", del.getOtherOutlayDesc() + "", del.getOtherOutlayPrice() + "")
                    .map(s -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(s));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        return cell;
                    })
                    .collect(Collectors.toList());

            for (int i = 0; i < keyPdfPCells.size(); i++) {
                table.addCell(keyPdfPCells.get(i));
                table.addCell(valuePdfPCells.get(i));
            }

            document.add(paragraph);
            document.add(table);
            document.close();

            return pathToPdf;
        } catch (Exception e) {
            System.out.println("Could not generate PDF");
            e.printStackTrace();

            return "";
        }
    }

}
