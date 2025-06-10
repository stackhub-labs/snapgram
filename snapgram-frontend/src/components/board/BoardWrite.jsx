import Input from "../common/Input";
import Button from "../common/Button";
import "../pages/BoardWritePage.css";
import PlusImage from "../../assets/plus-image.svg";
import UploadBoard from "../../assets/upload.svg";
import { useState, useRef } from "react";

const BoardWrite = () => {
    const fileInputRef = useRef(null);
    const [preview, setPreview] = useState(null);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file && file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    return (
        <div className ="input-tag-outline">
            <div className="input-tag">
                <Input type="file" 
                    accept='image/jpg, image/png, image/jpeg, image/gif'
                    name='board_upload_img'
                    style={{display: "none"}}
                    ref={fileInputRef}
                    onChange={handleFileChange}
                    />
                <Input placeholder={"게시글 입력란"}/>
                <Button style={{width: "50px"}}
                    onClick={() => fileInputRef.current.click()}>
                        <img src={PlusImage} alt="이미지 선택"/>
                </Button>
                <Button style={{width: "50px"}}>
                    <img src={UploadBoard} alt="업로드 아이콘"/>
                </Button>
            </div>
        </div>
    );
}
export default BoardWrite;

// https://moretz0921.tistory.com/770
// https://velog.io/@min1378/React-image-%EC%97%85%EB%A1%9C%EB%93%9Cs
// 이미지 인풋 태그에 onChange 이벤트 넣기
